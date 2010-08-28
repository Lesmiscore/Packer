/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package packer;

import packer.evaluators.Evaluator;
import packer.evaluators.DeleteEvaluator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import packer.evaluators.IntegerEvaluator;
import packer.evaluators.StringEvaluator;
import packer.strategies.DefaultReplacementStrategy;
import packer.strategies.ReplacementStrategy;

/**
 * 
 * @author Poly
 */
public class PackerParser {

    private static Pattern GROUPS = Pattern.compile("\\(");
    private static Pattern SUB_REPLACE = Pattern.compile("\\$(\\d+)");
    private static Pattern INDEXED = Pattern.compile("^\\$\\d+$");
    private static Pattern ESCAPE = Pattern.compile("\\\\.");
    private static Pattern ESCAPE_BRACKETS = Pattern.compile("\\(\\?[:=!]|\\[[^\\]]+\\]");
    private static Pattern DELETED = Pattern.compile("\\x01[^\\x01]*\\x01");
    private static String IGNORE = "$1";
    private boolean ignoreCase = false;
    public List<PackerPattern> patterns = new ArrayList<PackerPattern>();

    // / <summary>
    // / Add an expression to be deleted
    // / </summary>
    // / <param name="expression">Regular Expression String</param>
    public void remove(String expression) {
        replace(expression, "");
    }

    public void ignore(String expression) {
        replace(expression, IGNORE);
    }

    // / <summary>
    // / Add an expression to be replaced with the replacement string
    // / </summary>
    // / <param name="expression">Regular Expression String</param>
    // / <param name="replacement">Replacement String. Use $1, $2, etc. for
    // groups</param>
    public void replace(String expression, String replacement) {
        if (replacement.isEmpty()) {
            replace(expression, new DeleteEvaluator());
            return;
        }
        PackerPattern pattern = new PackerPattern(expression, new StringEvaluator(replacement));
        pattern.setLength(countGroups(expression));

        // does the pattern deal with sub-expressions? and a simple lookup (e.g. $2)
        if (SUB_REPLACE.matcher(replacement).matches() && INDEXED.matcher(replacement).matches()) {
            pattern.setEvaluator(new IntegerEvaluator(Integer.parseInt(replacement.substring(1)) - 1));
        }
        patterns.add(pattern);
    }

    // / <summary>
    // / Add an expression to be replaced using a callback function
    // / </summary>
    // / <param name="expression">Regular expression string</param>
    // / <param name="replacement">Callback function</param>
    public void replace(String expression, Evaluator evaluator) {
        PackerPattern pattern = new PackerPattern(expression, evaluator);
        pattern.setLength(countGroups(expression));
        patterns.add(pattern);
    }

    // / <summary>
    // / builds the patterns into a single regular expression
    // / </summary>
    // / <returns></returns>
    private Pattern buildPatterns() {
        StringBuilder rtrn = new StringBuilder();
        for (PackerPattern pattern : patterns) {
            rtrn.append(pattern).append("|");
        }
        rtrn.deleteCharAt(rtrn.length() - 1);
        return Pattern.compile(rtrn.toString(), ignoreCase ? Pattern.CASE_INSENSITIVE : 0);
    }

    public String exec(String input) {
        return exec(input, new DefaultReplacementStrategy());
    }

    // / <summary>
    // / Executes the parser
    // / </summary>
    // / <param name="input">input string</param>
    // / <returns>parsed string</returns>
    public String exec(String input, ReplacementStrategy replacement) {
        Matcher matcher = buildPatterns().matcher(input);
        StringBuffer sb = new StringBuffer(input.length());
        while (matcher.find()) {
            String rep = replacement.replace(getPatterns(), matcher);
            if (rep != null && !rep.isEmpty()) {
                rep = Matcher.quoteReplacement(rep);
            }
            matcher.appendReplacement(sb, rep);
        }
        matcher.appendTail(sb);
        return DELETED.matcher(sb).replaceAll("");
    }

    // count the number of sub-expressions
    private int countGroups(String expression) {
        int cont = 0;
        Matcher matcher = GROUPS.matcher(internalEscape(expression));
        while (matcher.find()) {
            cont++;
        }
        // add 1 because each group is itself a sub-expression
        return cont + 1;
    }

    private String internalEscape(String str) {
        return ESCAPE.matcher(str).replaceAll("").replaceAll(ESCAPE_BRACKETS.toString(), "");
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public List<PackerPattern> getPatterns() {
        return patterns;
    }
}
