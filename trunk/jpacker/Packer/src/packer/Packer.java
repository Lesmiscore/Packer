/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package packer;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import packer.encoders.BasicEncoder;
import packer.encoders.Encoder;
import packer.strategies.DefaultReplacementStrategy;
import packer.strategies.ReplacementStrategy;

/**
 * 
 * @author Poly
 */
public class Packer {

    // / <summary>
    // / The encoding level to use. See http://dean.edwards.name/packer/usage/
    // for more info.
    // / </summary>
    /**
     *
     */
    private PackerEncoding encoding = PackerEncoding.NORMAL;
    private static final String UNPACK = "eval(function(p,a,c,k,e,r){e=%5$s;if(!''.replace(/^/,String)){while(c--)r[%6$s]=k[c]"
            + "||%6$s;k=[function(e){return r[e]}];e=function(){return'\\\\w+'};c=1};while(c--)if(k[c])p=p."
            + "replace(new RegExp('\\\\b'+e(c)+'\\\\b','g'),k[c]);return p}('%1$s',%2$s,%3$s,'%4$s'.split('|'),0,{}))";

    public Packer() {
        setEncoding(PackerEncoding.NORMAL);
    }

    /**
     * Constructor
     *
     * @param encoding
     *            The encoding level for this instance
     * @param fastDecode
     *            Adds a subroutine to the output to speed up decoding
     * @param specialChars
     *            Replaces special characters
     */
    public Packer(PackerEncoding encoding) {
        setEncoding(encoding);
    }

    /**
     * Packs the script
     *
     * @param script
     *            The script to pack
     * @return The packed script
     */
    public String pack(String script) {
        script += "\n";
        script = minify(script);
        script = shrinkVariables(script);
        script = encode(script);
        return script;
    }

    // zero encoding - just removal of whitespace and comments
    private String minify(String script) {
        PackerParser parser = new PackerParser();
        DefaultReplacementStrategy defaultStrat = new DefaultReplacementStrategy();
        // protect data
        parser = addDataRegEx(parser);
        script = parser.exec(script, defaultStrat);
        // remove white-space
        parser = addWhiteSpaceRegEx(parser);
        script = parser.exec(script, defaultStrat);
        // clean
        parser = addCleanUpRegEx(parser);
        script = parser.exec(script, defaultStrat);
        // done
        return script;
    }

    private PackerParser addDataRegEx(PackerParser parser) {
        final String COMMENT1 = "(\\/\\/|;;;)[^\\n]*";
        final String COMMENT2 = "\\/\\*[^*]*\\*+([^\\/][^*]*\\*+)*\\/";
        final String REGEX = "\\/(\\\\[\\/\\\\]|[^*\\/])(\\\\.|[^\\/\\n\\\\])*\\/[gim]*";

        // Packer.CONTINUE
        parser.remove("\\\\\\r?\\n");

        parser.ignore("'(\\\\.|[^'\\\\])*'");
        parser.ignore("\"(\\\\.|[^\"\\\\])*\"");
        parser.ignore("\\/\\*@|@\\*\\/|\\/\\/@[^\\n]*\\n");
        parser.replace("(" + COMMENT1 + ")\\n\\s*(" + REGEX + ")?", "\n$4");
        parser.replace("(" + COMMENT2 + ")\\s*(" + REGEX + ")?", " $4");
        parser.replace("([\\[\\(\\^=,{}:;&|!*?])\\s*(" + REGEX + ")", "$2$3");
        // ([\\[ (\\^=,{}:;&|!*?])\\s*
        return parser;
    }

    private PackerParser addCleanUpRegEx(PackerParser parser) {
        parser.replace("\\(\\s*;\\s*;\\s*\\)", "(;;)");
        parser.ignore("throw[^};]+[};]"); // safari 1.3 bug
        parser.replace(";+\\s*([};])", "$2");
        parser.remove(";;[^\\n\\r]+[\\n\\r]");
        return parser;
    }

    private PackerParser addWhiteSpaceRegEx(PackerParser parser) {
        parser.replace("(\\d)\\s+(\\.\\s*[a-z\\$_\\[\\(])", "$2 $3");
        parser.replace("([+\\-])\\s+([+\\-])", "$2 $3");
        parser.replace("(\\b|\\$)\\s+(\\b|\\$)", "$2 $3");
        parser.replace("\\b\\s+\\$\\s+\\b", " $ ");
        parser.replace("\\$\\s+\\b", "$ ");
        parser.replace("\\b\\s+\\$", " $");
        parser.replace("\\b\\s+\\b", " ");
        parser.remove("\\s+");
        return parser;
    }

    private String shrinkVariables(String script) {
        final Pattern pattern = Pattern.compile("^[^'\"]\\/");
        // identify blocks, particularly identify function blocks (which define
        // scope)
        Pattern blockPattern = Pattern.compile("(function\\s*[\\w$]*\\s*\\(\\s*([^\\)]*)\\s*\\)\\s*)?(\\{([^{}]*)\\})");
        List<String> blocks = new ArrayList<String>(); // store program blocks
        // (anything between
        // braces {})

        final List<String> data = new ArrayList<String>(); // encoded strings
        // and regular
        // expressions

        PackerParser parser = new PackerParser();
        parser = addDataRegEx(parser);
        script = parser.exec(script, new ReplacementStrategy() {

            public String replace(List<PackerPattern> patterns, Matcher matcher) {
                String replacement = "#" + data.size();
                String string = matcher.group();
                if (pattern.matcher(string).find()) {
                    replacement = string.charAt(0) + replacement;
                    string = string.substring(1);
                }
                data.add(string);
                return replacement;
            }
        });

        do {
            // put the blocks back
            Matcher blockMatcher = blockPattern.matcher(script);
            StringBuffer sb = new StringBuffer();
            while (blockMatcher.find()) {
                blockMatcher.appendReplacement(sb, encodeBlock(blockMatcher, blocks));
            }
            blockMatcher.appendTail(sb);
            script = sb.toString();
        } while (blockPattern.matcher(script).find());

        while (Pattern.compile("~(\\d+)~").matcher(script).find()) {
            script = decodeBlock(script, blocks);
        }
        // put strings and regular expressions back
        Matcher storeMatcher = Pattern.compile("#(\\d+)").matcher(script);
        StringBuffer sb2 = new StringBuffer();
        while (storeMatcher.find()) {
            int num = Integer.parseInt(storeMatcher.group(1));
            storeMatcher.appendReplacement(sb2, Matcher.quoteReplacement(data.get(num)));
        }
        storeMatcher.appendTail(sb2);

        return sb2.toString();
    }

    private String encode(String script) {
        PackerWords words = new PackerWords(script, encoding);

        Pattern wordsPattern = Pattern.compile("\\w+");
        Matcher wordsMatcher = wordsPattern.matcher(script);
        StringBuffer sb = new StringBuffer();
        while (wordsMatcher.find()) {
            PackerWord tempWord = new PackerWord(wordsMatcher.group());
            wordsMatcher.appendReplacement(sb, words.find(tempWord).getEncoded());
        }
        wordsMatcher.appendTail(sb);

        int ascii = Math.min(Math.max(words.getWords().size(), 2), encoding.getEncodingBase());

        String p = escape(sb.toString());
        String a = String.valueOf(ascii);
        String c = String.valueOf(words.getWords().size());
        String k = words.toString();
        String e = getEncode(ascii);
        String r = ascii > 10 ? "e(c)" : "c";

        return new Formatter().format(UNPACK, p, a, c, k, e, r).toString();
    }

    // encoder for program blocks
    private String encodeBlock(Matcher matcher, List<String> blocks) {

        String block = matcher.group();
        String func = matcher.group(1);
        String args = matcher.group(2);

        if (func != null && !func.isEmpty()) { // the block is a function block
            // decode the function block (THIS IS THE IMPORTANT BIT)
            // We are retrieving all sub-blocks and will re-parse them in light
            // of newly shrunk variables
            while (Pattern.compile("~(\\d+)~").matcher(block).find()) {
                block = decodeBlock(block, blocks);
            }

            // create the list of variable and argument names
            Pattern varNamePattern = Pattern.compile("var\\s+[\\w$]+");
            Matcher varNameMatcher = varNamePattern.matcher(block);
            StringBuilder sb = new StringBuilder();
            while (varNameMatcher.find()) {
                sb.append(varNameMatcher.group()).append(",");
            }

            String vars = "";
            if (!sb.toString().isEmpty()) {
                vars = sb.deleteCharAt(sb.length() - 1).toString().replaceAll("var\\s+", "");
            }

            String[] ids = concat(args.split("\\s*,\\s*"), vars.split("\\s*,\\s*"));
            Set<String> idList = new LinkedHashSet<String>();
            for (String s : ids) {
                if (!s.isEmpty()) {
                    idList.add(s);
                }
            }
            // process each identifier
            int count = 0;
            String shortId;
            for (String id : idList) {
                id = id.trim();
                if (id.length() > 1) { // > 1 char
                    id = Matcher.quoteReplacement(id);
                    // find the next free short name (check everything in the
                    // current scope)
                    Encoder e = new BasicEncoder();
                    do {
                        shortId = e.encode(count++);
                    } while (Pattern.compile("[^\\w$.]" + shortId + "[^\\w$:]").matcher(block).find());
                    // replace the long name with the short name
                    while (Pattern.compile("([^\\w$.])" + id + "([^\\w$:])").matcher(block).find()) {
                        block = block.replaceAll("([^\\w$.])" + id + "([^\\w$:])", "$1" + shortId + "$2");
                    }
                    block = block.replaceAll("([^{,\\w$.])" + id + ":", "$1" + shortId + ":");
                }
            }
        }
        String replacement = "~" + blocks.size() + "~";
        blocks.add(block);
        return replacement;
    }

    private String decodeBlock(String block, List<String> blocks) {
        Matcher encoded = Pattern.compile("~(\\d+)~").matcher(block);
        StringBuffer sbe = new StringBuffer();
        while (encoded.find()) {
            int num = Integer.parseInt(encoded.group(1));
            encoded.appendReplacement(sbe, Matcher.quoteReplacement(blocks.get(num)));
        }
        encoded.appendTail(sbe);
        return sbe.toString();
    }

    private String[] concat(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    private String getEncode(int ascii) {
        if (ascii > 96) {
            return PackerEncoding.HIGH_ASCII.getEncode();
        } else if (ascii > 36) {
            return PackerEncoding.NORMAL.getEncode();
        } else if (ascii > 10) {
            return PackerEncoding.MID.getEncode();
        } else {
            return PackerEncoding.NUMERIC.getEncode();
        }
    }

    private String escape(String input) {
        // single quotes wrap the final string so escape them
        // also escape new lines required by conditional comments
        return input.replaceAll("([\\\\'])", "\\\\$1").replaceAll("[\\r\\n]+", "\\n");
    }

    /**
     * Encoding level. Options are: None, Numeric, Mid, Normal and High-Ascii.
     *
     * @return The current encoding level
     */
    public PackerEncoding getEncoding() {
        return encoding;
    }

    /**
     *
     * @param encoding
     */
    public final void setEncoding(PackerEncoding encoding) {
        this.encoding = encoding;
    }
}
