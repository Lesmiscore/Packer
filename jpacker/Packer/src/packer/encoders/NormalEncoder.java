package packer.encoders;

/**
 * 
 * @author Poly
 */
public class NormalEncoder implements Encoder {

	public String encode(int c) {						
		return (c < 62 ? "" : encode(c / 62)) + ((c = c % 62) > 35 ? String.valueOf((char)(c + 29)) : Integer.toString(c, 36));
	}

}
