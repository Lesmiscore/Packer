/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package packer.evaluators;

import java.util.regex.Matcher;
import packer.PackerPattern;

/**
 *
 * @author poly
 */
public class StringEvaluator implements Evaluator {

    private String replacement;

    public StringEvaluator(String replacement) {
        this.replacement = replacement;
    }

    // / <summary>
    // / Replacement function for complicated lookups (e.g. Hello $3 $2)
    // / </summary>
    public String evaluate(PackerPattern pattern, Matcher matcher, int offset) {
        int length = pattern.getLength();
        while (length-- > 0) {
            String matchedGroup = matcher.group(offset + length) == null ? "" : matcher.group(offset + length);
            replacement = replacement.replace("$" + (length + 1), matchedGroup);
        }
        return replacement;
    }
}
