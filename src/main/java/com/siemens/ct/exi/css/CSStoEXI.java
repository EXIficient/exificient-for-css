/*
 * Copyright (c) 2007-2016 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.css;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.xml.transform.TransformerConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.exceptions.EXIException;

public class CSStoEXI {

	public static void main(String[] args) throws IOException, EXIException, SAXException, TransformerConfigurationException {
		if(args == null || args.length != 1) {
			System.out.println("Usage: <executable> input.css");
		} else {
			CSStoEXI css2Exi = new CSStoEXI();
			String sXML = args[0] + CSSConstants.SUFFIX_XML;
			String sEXI = args[0] + CSSConstants.SUFFIX_EXI;
			
			css2Exi.generate(args[0], CSSConstants.EXI_FACTORY, sXML, sEXI);
			System.out.println("CSS '" + args[0] + "'(" + new File(args[0]).length() + ") to EXI '" + sEXI + "'(" + new File(sEXI).length() + ")");
		}
	}
	
	public void generate(String fCSS, EXIFactory exiFactory, String fXML, String fEXI) throws IOException, EXIException, SAXException, TransformerConfigurationException {
		// Note: currently a two step process to see XML also
		// CSS -> XML -> EXI
		// Can and should be switched to directly produce EXI
		
		// 1. CSS to XML
		InputStream isCSS = new FileInputStream(fCSS);
		OutputStream osXML = new FileOutputStream(fXML);
		
		CSStoXML css2xml = new CSStoXML();
		css2xml.generate(isCSS, new PrintStream(osXML));
		osXML.close();
		
		// 2. XML to EXI
		OutputStream osEXI = new FileOutputStream(fEXI);

		EXIResult exiResult = new EXIResult(exiFactory);
		exiResult.setOutputStream(osEXI);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler( exiResult.getHandler() );
		xmlReader.parse(fXML); // parse XML input
		osEXI.close();
		
	}

	
}