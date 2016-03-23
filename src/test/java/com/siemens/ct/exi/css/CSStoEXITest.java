package com.siemens.ct.exi.css;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.xml.transform.TransformerConfigurationException;

import junit.framework.TestCase;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.exceptions.EXIException;

public class CSStoEXITest extends TestCase {
	
	PrintStream ps = System.out;
	
	private static boolean setUpIsDone = false;
	
	public void setUp() {
		//executed only once, before the first test
	    if (!setUpIsDone) {
	    	ps.println("CSS-Name; SizeCSS; SizeEXI");
	    	setUpIsDone = true;
	    }
    }
	
	protected void _test(String css) throws IOException, EXIException, SAXException, TransformerConfigurationException {
		CSStoEXI css2Exi = new CSStoEXI();
		String sXML = File.createTempFile("css", "xml").getAbsolutePath();
		String sEXI = File.createTempFile("css", "exi").getAbsolutePath();
		css2Exi.generate(css, sXML, sEXI);
		System.out.println(css + ";" + new File(css).length() + ";" + new File(sEXI).length());
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

}
