/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package packer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Poly
 */
public final class PackerWords {

    private PackerEncoding encoding;
    private static final Pattern WORDS = Pattern.compile("\\w+");
    private List<PackerWord> words = new ArrayList<PackerWord>();

    public PackerWords(String script, PackerEncoding encoding) {
        this.encoding = encoding;
        Matcher matcher = WORDS.matcher(script);
        while (matcher.find()) {
            add(new PackerWord(matcher.group()));
        }
        encode();
    }

    public void add(PackerWord word) {
        if (!words.contains(word)) {            
            words.add(word);
        }
        PackerWord w = find(word);
        w.setCount(w.getCount() + 1);
    }

    private void encode() {
        // sort by frequency
        Collections.sort(words, new Comparator<PackerWord>() {

            public int compare(PackerWord x, PackerWord y) {
                return y.getCount() - x.getCount();
            }
        });

        Map<String, Integer> encoded = new HashMap<String, Integer>(); // a dictionary of base62 -> base10

        for (int i = 0; i < words.size(); i++) {
            encoded.put(encoding.getEncoder().encode(i), i);
        }
        
        int index = 0;
        for (PackerWord word : words) {
            if (encoded.containsKey(word.getWord())) {
                word.setIndex(encoded.get(word.getWord()));
                word.setReplacement("");
            } else {
                while (words.contains(new PackerWord(encoding.getEncoder().encode(index)))) {
                    index++;
                }
                word.setIndex(index++);
                word.setReplacement(word.getWord());
            }            
            word.setEncoded(encoding.getEncoder().encode(word.getIndex()));
        }

        // sort by encoding
        Collections.sort(words, new Comparator<PackerWord>() {

            public int compare(PackerWord x, PackerWord y) {
                return x.getIndex() - y.getIndex();
            }
        });
        
    }

    public PackerWord find(PackerWord word) {
        Iterator<PackerWord> it = words.iterator();
        while (it.hasNext() == true) {
            PackerWord pw = it.next();
            if (pw.equals(word)) {
                return pw;
            }
        }
        return null;
    }

    public List<PackerWord> getWords() {
        return words;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (PackerWord word : words) {
            sb.append(word.getReplacement()).append('|');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
