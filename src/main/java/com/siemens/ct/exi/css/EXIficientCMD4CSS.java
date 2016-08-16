package com.siemens.ct.exi.css;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.cmd.CmdOption;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class EXIficientCMD4CSS {

	static final PrintStream ps = System.out;

	public static final String HELP = "-h";

	public static String ENCODE = "-encode";
	public static String DECODE = "-decode";

	public static final String CODING_COMPRESSION = "-compression";

	EXIFactory exiFactory;
	boolean inputParametersOK;
	CmdOption cmdOption;
	String input;
	String output;
	String outputXML;

	private static void printHeader() {
		ps.println("#########################################################################");
		ps.println("###   EXIficient for CSS                                             ###");
		ps.println("###   Command-Shell Options                                          ###");
		ps.println("#########################################################################");
	}

	private static void printHelp() {
		printHeader();

		ps.println();
		ps.println(" " + HELP
				+ "                               /* shows help */");
		ps.println();
		ps.println(" " + ENCODE);
		ps.println(" " + DECODE);
		ps.println();
		ps.println(" " + CODING_COMPRESSION);
		ps.println();
		ps.println("# Examples");
		ps.println(" " + ENCODE + " sample.css");
		ps.println(" " + DECODE + " sample.css.xml.exi");
	}

	protected static void printError(String msg) {
		ps.println("[ERROR] " + msg);
	}

	protected void parseArguments(String[] args) throws EXIException {
		// arguments that need to be set
		cmdOption = null;

		input = null;
		output = null;

		exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setFidelityOptions(FidelityOptions.createStrict());
		exiFactory.setGrammars(CSSConstants.EXI_FOR_CSS_GRAMMARS); // use XML
																	// schema

		int indexArgument = 0;
		while (indexArgument < args.length) {
			String argument = args[indexArgument];

			// ### HELP ?
			if (HELP.equalsIgnoreCase(argument)) {
				printHelp();
				break;
			}
			// ### CODING_OPTIONS
			else if (ENCODE.equalsIgnoreCase(argument)) {
				cmdOption = CmdOption.encode;
			} else if (DECODE.equalsIgnoreCase(argument)) {
				cmdOption = CmdOption.decode;
			}
			// ### COMPRESSION
			else if (CODING_COMPRESSION.equalsIgnoreCase(argument)) {
				exiFactory.setCodingMode(CodingMode.COMPRESSION);
			} else {
				System.out.println("Unknown option '" + argument + "'");
			}

			indexArgument++;
		}

		// check input
		inputParametersOK = true;

		if (cmdOption == null) {
			inputParametersOK = false;
			printError("Missing coding option such as " + ENCODE + " and "
					+ DECODE);
		}

		if (input == null) {
			inputParametersOK = false;
			printError("Missing option -i");
		} else if (!(new File(input)).exists()) {
			inputParametersOK = false;
			printError("Not existing input parameter -i, \"" + input + "\"");
		} else {
			// ok
		}

		if (input != null && output == null) {
			// default output
			if (CmdOption.encode == cmdOption) {
				outputXML = input + CSSConstants.SUFFIX_XML;
				output = input + CSSConstants.SUFFIX_EXI;

			} else {
				output = input + CSSConstants.SUFFIX_CSS;
			}
		}

		File fOutput = null;
		if (output == null) {
			inputParametersOK = false;
			printError("Missing output specification!");
		} else {
			fOutput = new File(output);

			if (fOutput.isDirectory()) {
				inputParametersOK = false;
				printError("Outputfile '" + output
						+ "' is unexpectedly a directory");
			} else {
				if (fOutput.exists()) {
					if (!fOutput.delete()) {
						inputParametersOK = false;
						printError("Existing outputfile '" + output
								+ "' could not be deleted.");
					}
				} else {
					// does not exits
					assert (!fOutput.exists());
					File parentDir = fOutput.getParentFile();
					if (parentDir != null && !parentDir.exists()) {
						if (!parentDir.mkdirs()) {
							inputParametersOK = false;
							printError("Output directories for file '" + output
									+ "' could not be created.");
						}
					}
				}
			}
		}
	}

	protected void decode(String input, EXIFactory exiFactory, String output)
			throws FileNotFoundException, EXIException, IOException {

		EXItoCSS exi2CSS = new EXItoCSS();
		exi2CSS.generate(input, exiFactory, output);
	}

	protected void encode(String input, EXIFactory exiFactory,
			String outputXML, String output)
			throws TransformerConfigurationException, IOException,
			EXIException, SAXException {
		CSStoEXI css2EXI = new CSStoEXI();
		css2EXI.generate(input, exiFactory, outputXML, output);
	}

	protected void process() throws EXIException, TransformerException,
			IOException, SAXException {
		if (inputParametersOK) {
			// start coding
			switch (cmdOption) {
			case decode:
				decode(input, exiFactory, output);
				break;
			case encode:
				encode(input, exiFactory, outputXML, output);
				break;
			default:
				printError("Unexptected command option " + cmdOption);
				break;
			}

		} else {
			// something not right
			// info messages (see above)
			printError("Input parameters were incorrect");
		}
	}

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			// show help
			printHelp();
		} else {
			EXIficientCMD4CSS cmd = new EXIficientCMD4CSS();
			try {
				cmd.parseArguments(args);
				cmd.process();
			} catch (Exception e) {
				printError(e.getLocalizedMessage() + e.getClass());
			}
		}
	}

}
