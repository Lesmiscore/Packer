/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package packer.encoders;

/**
 * 
 * @author Poly
 */
public class MidEncoder implements Encoder {

	// lookups seemed like the easiest way to do this since
	// I don't know of an equivalent to .toString(36)
	private static String	LOOKUP_36	= "0123456789abcdefghijklmnopqrstuvwxyz";

	public String encode(int code) {
		String encoded = "";
		int i = 0;
		do {
			int digit = (code / (int) Math.pow(36, i)) % 36;
			encoded = LOOKUP_36.charAt(digit) + encoded;
			code -= digit * (int) Math.pow(36, i++);
		} while (code > 0);
		return encoded;
	}
}
