/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package packer.encoders;

/**
 *
 * @author Poly
 */
public class NumericEncoder implements Encoder {

    public String encode(int code) {
        return String.valueOf(code);
    }

}
