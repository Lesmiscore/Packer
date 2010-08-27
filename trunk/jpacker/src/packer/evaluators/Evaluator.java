/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package packer.evaluators;

import java.util.regex.Matcher;
import packer.PackerPattern;

/**
 *
 * @author Poly
 */
public interface Evaluator {

    public String evaluate(PackerPattern pattern, Matcher matcher, int offset);

}
