/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package packer.encoders;

/**
 *
 * @author Poly
 */
public class HighAsciiEncoder implements Encoder {

    private static String LOOKUP_95 = "¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏĞÑÒÓÔÕÖ×ØÙÚÛÜİŞßàáâãäåæçèéêëìíîïğñòóôõö÷øùúûüışÿ";

    public String encode(int code) {
        String encoded = "";
        int i = 0;
        do {
            int digit = (code / (int) Math.pow(95, i)) % 95;
            encoded = LOOKUP_95.charAt(digit) + encoded;
            code -= digit * (int) Math.pow(95, i++);
        } while (code > 0);
        return encoded;
    }
}
