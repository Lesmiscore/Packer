/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package packer;

/**
 *
 * @author Poly
 */
public class PackerWord {

    private int count = 0;
    private String encoded = "";
    private int index = -1;
    private String word;
    private String replacement;

    public PackerWord(String word) {
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getEncoded() {
        return encoded;
    }

    public void setEncoded(String encoded) {
        this.encoded = encoded;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PackerWord other = (PackerWord) obj;
        if ((this.word == null) ? (other.word != null) : !this.word.equals(other.word)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.word != null ? this.word.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return word;
    }
}
