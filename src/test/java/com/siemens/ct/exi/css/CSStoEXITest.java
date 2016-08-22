package com.siemens.ct.exi.css;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;

import javax.xml.transform.TransformerConfigurationException;

import junit.framework.TestCase;

import org.junit.Test;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleSheet;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.exceptions.EXIException;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import com.yahoo.platform.yui.compressor.CssCompressor;

public class CSStoEXITest extends TestCase {
	
	PrintStream ps = System.out;
	
	private static boolean setUpIsDone = false;
	
	public void setUp() {
		//executed only once, before the first test
	    if (!setUpIsDone) {
	    	ps.println("CSS-Name; SizeCSS; SizeCompressedCSS; SizeEXI");
	    	setUpIsDone = true;
	    }
    }
	
	protected void _test(String css) throws IOException, EXIException, SAXException, TransformerConfigurationException {
		CSStoEXI css2Exi = new CSStoEXI();
		String sXML = File.createTempFile("css", "xml").getAbsolutePath();
		String sEXI = File.createTempFile("css", "exi").getAbsolutePath();
		css2Exi.generate(css, CSSConstants.EXI_FACTORY, sXML, sEXI);
		Reader cssIn = new FileReader(new File(css));
		CssCompressor cssCompressor = new CssCompressor(cssIn);
		File fCssOut = File.createTempFile("cssCompressor", "css");
		Writer cssOut = new FileWriter(fCssOut);
		cssCompressor.compress(cssOut, -1);
		cssOut.flush();
		System.out.println(css + "; " + new File(css).length() + "; " + fCssOut.length() + "; " + new File(sEXI).length());
		
		// read it again
		EXItoCSS exi2Css = new EXItoCSS();
		String sCSS = File.createTempFile("exi", "css").getAbsolutePath();
		exi2Css.generate(sEXI, CSSConstants.EXI_FACTORY, sCSS);
		
		InputSource source = new InputSource(new FileReader(sCSS));
		CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
		// parse and create a stylesheet composition
		CSSStyleSheet stylesheet = parser.parseStyleSheet(source, null, null);
		
		assertFalse("Decoded CSS failed", stylesheet == null);
		
	}
	
	@Test
	public void testDesign() throws FileNotFoundException, IOException, EXIException, SAXException, TransformerConfigurationException {
		_test("data/design.css");
	}
	
	@Test
	public void testModule() throws FileNotFoundException, IOException, EXIException, SAXException, TransformerConfigurationException {
		_test("data/module.css");
	}
	
	@Test
	public void testCSS2_1() throws FileNotFoundException, IOException, EXIException, SAXException, TransformerConfigurationException {
		_test("data/css2_1.css");
	}

	
	@Test
	public void testCSS3_1() throws FileNotFoundException, IOException, EXIException, SAXException, TransformerConfigurationException {
		_test("data/css3_1.css");
	}
	
	@Test
	public void testW3C() throws FileNotFoundException, IOException, EXIException, SAXException, TransformerConfigurationException {
		_test("data/w3c.css");
	}
	
	
	@Test
	public void testRules() throws FileNotFoundException, IOException, EXIException, SAXException, TransformerConfigurationException {
		_test("data/rules.css");
	}
	
	@Test
	public void testAmazon() throws FileNotFoundException, IOException, EXIException, SAXException, TransformerConfigurationException {
		_test("data/amazon.css");
	}
	
	@Test
	// Bank of America
	public void testBoa() throws FileNotFoundException, IOException, EXIException, SAXException, TransformerConfigurationException {
		_test("data/boa.css");
	}
	
	@Test
	public void testCapitalone() throws FileNotFoundException, IOException, EXIException, SAXException, TransformerConfigurationException {
		_test("data/capitalone.css");
	}
	
	@Test
	public void testFujitsu() throws FileNotFoundException, IOException, EXIException, SAXException, TransformerConfigurationException {
		_test("data/fujitsu.css");
	}
	
	@Test
	// Wall Street Journal
	public void testWSJ() throws FileNotFoundException, IOException, EXIException, SAXException, TransformerConfigurationException {
		_test("data/wsj.css");
	}
	
	@Test
	// Wall Street Journal
	public void testYahooJapan() throws FileNotFoundException, IOException, EXIException, SAXException, TransformerConfigurationException {
		_test("data/yahoo_japan.css");
	}

}
