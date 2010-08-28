/**
 * Packer version 3.0 (final)
 * Copyright 2004-2007, Dean Edwards
 * Web: {@link http://dean.edwards.name/}
 * 
 * This software is licensed under the MIT license
 * Web: {@link http://www.opensource.org/licenses/mit-license}
 * 
 * Ported to Java by Pablo Santiago based on C# version by Jesse Hansen, <twindagger2k @ msn.com>
 * Web: {@link http://jpacker.googlecode.com/}
 * Email: <pablo.santiago @ gmail.com>
 */
package com.jpacker;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.IllegalOptionValueException;
import jargs.gnu.CmdLineParser.Option;
import jargs.gnu.CmdLineParser.UnknownOptionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * 
 * @author Pablo Santiago <pablo.santiago @ gmail.com>
 */
public class JPacker {

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {

		double startTime = System.currentTimeMillis();
		CmdLineParser parser = new CmdLineParser();
		Option outputFilenameOpt = parser.addStringOption('o', "output");
		Option baseOpt = parser.addIntegerOption('b', "base");
		Option columnsOpt = parser.addIntegerOption('c', "column");
		Option helpOpt = parser.addBooleanOption('h', "help");
		Option minifyOpt = parser.addBooleanOption('m', "minify");
		Option shrinkVariablesOpt = parser.addBooleanOption('s', "shrink-variables");

		Writer out = null;
		BufferedReader in = null;

		try {
			parser.parse(args);

			Boolean help = (Boolean) parser.getOptionValue(helpOpt);
			if (help != null && help.booleanValue()) {
				printUsage();
				System.exit(0);
			}

			boolean minify = parser.getOptionValue(minifyOpt) != null;
			boolean shrinkVariables = parser.getOptionValue(shrinkVariablesOpt) != null;
			Integer base = (Integer) parser.getOptionValue(baseOpt);
			Integer columns = (Integer) parser.getOptionValue(columnsOpt);
			String inputFilename = parser.getRemainingArgs()[0];
			String outputFilename = (String) parser.getOptionValue(outputFilenameOpt);

			JPackerExecuter executer;
			if (base == null) {
				minify = true;
				executer = new JPackerExecuter(JPackerEncoding.NONE);
			} else {
				executer = new JPackerExecuter(getEncoding(baseOpt, base));
			}
			in = new BufferedReader(new FileReader(new File(inputFilename)));
			String unpacked = buildStringFromTextFile(in);

			String packed = executer.pack(unpacked, minify, shrinkVariables);

			if (outputFilename == null) {
				out = new OutputStreamWriter(System.out);
			} else {
				out = new OutputStreamWriter(new FileOutputStream(outputFilename));
			}

			if (columns != null) {
				packed = wrapLines(packed, columns);
			}

			out.write(packed.replace("\n", "\r\n"));
			out.close();

			double endTime = System.currentTimeMillis();
			System.out.printf("Reduced to %.2f%% of its original size in %.4f seconds.\nOutput file: %s",
					((double) packed.length() / (double) unpacked.length()) * 100, (endTime - startTime) / 1000,
					outputFilename);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IllegalOptionValueException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (UnknownOptionException e) {
			printUsage();
			System.exit(1);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void printUsage() {
		System.out.println("\nUsage: java -jar jpacker-x.y.z.jar [options] [input file]\n\n"

		+ "Options\n"
				+ "  -b, --base                      Encoding base. Options are: 10, 36, 52, 62 and 95. Defaults to 62.\n"
				+ "  -c, --column <column>           Insert a line break after the specified column number.\n"
				+ "  -h, --help                      Displays this information\n"
				+ "  -m, --minify                    Minify only, do not obfuscate\n"
				+ "  -o <file>, --output <file>      Place the output into <file>. Defaults to stdout.\n"
				+ "  -s, --shrink-variables          Shrinks variables.");
	}

	private static String wrapLines(String packedScript, Integer columns) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < packedScript.length(); i++) {
			int end = ((i + columns) > (packedScript.length()) ? packedScript.length() : i + columns);
			sb.append(packedScript.substring(i, end)).append(System.getProperty("line.separator"));
			i = end + 1;
		}
		return sb.toString();
	}

	private static String buildStringFromTextFile(BufferedReader reader) throws FileNotFoundException, IOException {
		StringBuilder sb = new StringBuilder();
		String s;
		while ((s = reader.readLine()) != null) {
			sb.append(s).append(System.getProperty("line.separator"));
		}
		reader.close();
		return sb.toString();
	}

	private static JPackerEncoding getEncoding(Option option, Integer base) throws IllegalOptionValueException {
		switch (base) {
			case 10:
				return JPackerEncoding.NUMERIC;
			case 36:
				return JPackerEncoding.MID;
			case 52:
				return JPackerEncoding.BASIC;
			case 62:
				return JPackerEncoding.NORMAL;
			case 95:
				return JPackerEncoding.HIGH_ASCII;
			default:
				throw new IllegalOptionValueException(option, "Encoding base option not valid");
		}
	}
}