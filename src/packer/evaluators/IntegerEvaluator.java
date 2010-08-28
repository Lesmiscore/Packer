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
public class IntegerEvaluator implements Evaluator {

    private int replacement;

    public IntegerEvaluator(int replacement) {
        this.replacement = replacement;
    }

    public String evaluate(PackerPattern pattern, Matcher matcher, int offset) {
        return matcher.group(replacement + offset);
    }

}
