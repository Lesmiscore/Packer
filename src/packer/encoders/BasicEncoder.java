package packer.encoders;

public class BasicEncoder implements Encoder {

	@Override
	public String encode(int c) {	
		return (c < 52 ? "" : encode(c / 52)) + (((c = c % 52)) > 25 ? String.valueOf((char) (c + 39)) : String.valueOf((char) (c + 97)));
	}

}
