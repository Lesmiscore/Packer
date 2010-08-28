package packer;

import packer.encoders.Encoder;
import packer.encoders.HighAsciiEncoder;
import packer.encoders.MidEncoder;
import packer.encoders.NormalEncoder;
import packer.encoders.NumericEncoder;

public enum PackerEncoding {

    NONE(0, "", null),
    NUMERIC(10, "String", new NumericEncoder()),
    MID(36, "function(c){return c.toString(a)}", new MidEncoder()),
    NORMAL(62, "function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))}", new NormalEncoder()),
    HIGH_ASCII(95, "function(c){return(c<a?\"\":e(c/a))String.fromCharCode(c%a+161)}", new HighAsciiEncoder());
    private final int encodingBase;
    private final String encode;
    private Encoder encoder;

    PackerEncoding(int encodingBase, String encode, Encoder encoder) {
        this.encodingBase = encodingBase;
        this.encode = encode;
        this.encoder = encoder;
    }

    public int getEncodingBase() {
        return encodingBase;
    }

    public String getEncode() {
        return encode;
    }

    public Encoder getEncoder() {
        return encoder;
    }
}
