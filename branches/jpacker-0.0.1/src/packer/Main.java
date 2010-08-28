/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package packer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 
 * @author Poly
 */
public class Main {

	private static final int	CHARACTERS	= 200;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Packer p = new Packer();
		File file = new File("/home/poly/JavaScript/jquery-1.4.2.js");

		String unpacked = buildStringFromTextFile(file);
		String packer = p.pack(unpacked);
		int originalQty = unpacked.length();
		int packedQty = packer.length();
		System.out.println(packer.replace("\n", "\r\n"));
		System.out.println(("%"+((double)packedQty /(double)originalQty)*100));

		/*File myPackerFile = new File("C:\\Documents and Settings\\pdsanti\\Desktop\\JavaVersion.txt");
		File jqueryFile = new File("C:\\Documents and Settings\\pdsanti\\Desktop\\JavaScriptVersion.txt");
		File jqueryPackedFile = new File("C:\\Documents and Settings\\pdsanti\\Desktop\\JQueryPacked.txt");

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(myPackerFile)));
		out.write(wrapLines(packer));
		out.close();

		String s = buildStringFromTextFile(jqueryPackedFile);
		PrintWriter out2 = new PrintWriter(new BufferedWriter(new FileWriter(jqueryFile)));
		out2.write(wrapLines(s));
		out2.close();*/

	}

	private static String wrapLines(String packedScript) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < packedScript.length(); i++) {
			int end = ((i + CHARACTERS) > (packedScript.length()) ? packedScript.length() : i + CHARACTERS);
			sb.append(packedScript.substring(i, end)).append(System.getProperty("line.separator"));
			i = end + 1;
		}
		return sb.toString();
	}

	private static String buildStringFromTextFile(File file) {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String s;
			while ((s = reader.readLine()) != null) {
				sb.append(s).append(System.getProperty("line.separator"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
