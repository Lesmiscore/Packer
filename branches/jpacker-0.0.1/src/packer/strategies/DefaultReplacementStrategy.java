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
public class DefaultReplacementStrategy implements ReplacementStrategy {

	// / <summary>
	// / Global replacement function. Called once for each match found
	// / </summary>
	// / <param name="match">Match found</param>
	public String replace(List<PackerPattern> patterns, Matcher matcher) {
		int i = 1;
		// loop through the patterns
		for (PackerPattern pattern : patterns) {
			// do we have a result?
			if (isMatch(matcher.group(i))) {
                                return pattern.getEvaluator().evaluate(pattern, matcher, i);
			} else { // skip over references to sub-expressions
				i += pattern.getLength();
			}
		}
		return matcher.group(); // should never be hit, but you never know
	}

        private boolean isMatch(String match){
            return (match != null && !match.isEmpty());
        }
}
