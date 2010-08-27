/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package packer.strategies;

import java.util.List;
import java.util.regex.Matcher;
import packer.PackerPattern;

/**
 *
 * @author Poly
 */
public interface ReplacementStrategy {

    public String replace(List<PackerPattern> pattern, Matcher matcher);
}
