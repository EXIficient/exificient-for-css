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

import com.steadystate.css.parser.CSSOMParser;

import org.w3c.css.sac.InputSource;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSCharsetRule;
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSPageRule;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSUnknownRule;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.stylesheets.MediaList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.*;
import java.util.StringTokenizer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

public class CSStoXML {
	
	static final String URI = "";
	
	final Attributes attsEmpty = new AttributesImpl();

	public static void main(String[] args) throws IOException, TransformerConfigurationException, SAXException {
		if (args == null || args.length != 1) {
			System.out.println("Usage: <executable> input.css");
		} else {
			CSStoXML css2xml = new CSStoXML();
			InputStream is = new FileInputStream(args[0]);
			css2xml.generate(is);
		}
	}
	
	public CSStoXML() {
	}
	
	static final String primitiveTagNames[] = {
		// public static final short CSS_UNKNOWN = 0;
		"cssUnknown",
		// public static final short CSS_NUMBER = 1;
		"cssNumber",
		// public static final short CSS_PERCENTAGE = 2;
		"cssPercentage",
		// public static final short CSS_EMS = 3;
		"cssEms",
		// public static final short CSS_EXS = 4;
		"cssExs",
		// public static final short CSS_PX = 5;
		"cssPx",
		// public static final short CSS_CM = 6;
		"cssCm",
		// public static final short CSS_MM = 7;
		"cssMm",
		// public static final short CSS_IN = 8;
		"cssIn",
		// public static final short CSS_PT = 9;
		"cssPt",
		// public static final short CSS_PC = 10;
		"cssPc",
		// public static final short CSS_DEG = 11;
		"cssDeg",
		// public static final short CSS_RAD = 12;
		"cssRad",
		// public static final short CSS_GRAD = 13;
		"cssGrad",
		// public static final short CSS_MS = 14;
		"cssMs",
		// public static final short CSS_S = 15;
		"cssS",
		// public static final short CSS_HZ = 16;
		"cssHz",
		// public static final short CSS_KHZ = 17;
		"cssKhz",
		// public static final short CSS_DIMENSION = 18;
		"cssDimension",
		// public static final short CSS_STRING = 19;
		"cssString",
		// public static final short CSS_URI = 20;
		"cssUri",
		// public static final short CSS_IDENT = 21;
		"cssIdent",
		// public static final short CSS_ATTR = 22;
		"cssAttr",
		// public static final short CSS_COUNTER = 23;
		"cssCounter",
		// public static final short CSS_RECT = 24;
		"cssRect",
		// public static final short CSS_RGBCOLOR = 25;
		"cssRgbColor" };

	public void generate(InputStream stream) throws IOException, TransformerConfigurationException, SAXException {
		PrintStream ps = System.out;
		generate(stream, ps);
	}

	public void generate(InputStream stream, PrintStream ps) throws IOException, TransformerConfigurationException, SAXException {
		InputSource source = new InputSource(new InputStreamReader(stream));
		CSSOMParser parser = new CSSOMParser();
		// parse and create a stylesheet composition
		CSSStyleSheet stylesheet = parser.parseStyleSheet(source, null, null);

		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory
				.newInstance();
		TransformerHandler th = tf.newTransformerHandler();
		Transformer tranformer = th.getTransformer();
		tranformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult xmlResult = new StreamResult(ps);
		th.setResult(xmlResult);
		
		th.startDocument();
		printStartElement(th, URI, "stylesheet");

		CSSRuleList ruleList = stylesheet.getCssRules();
		generateCSSRuleList(ruleList, th);

		printEndElement(th, URI, "stylesheet");
		th.endDocument();
	}
	
	protected void generateMediaList(MediaList mediaList, TransformerHandler th) throws SAXException {
		printStartElement(th, URI, "mediaList");
		for(int k=0;k<mediaList.getLength(); k++) {
			printCharacters(th, mediaList.item(k));
		}
		printEndElement(th, URI, "mediaList");
	}
	
	protected void generateCSSRuleList(CSSRuleList ruleList, TransformerHandler th) throws SAXException {
		for (int i = 0; i < ruleList.getLength(); i++) {
			CSSRule rule = ruleList.item(i);
			switch (rule.getType()) {
			case CSSRule.STYLE_RULE:
				CSSStyleRule styleRule = (CSSStyleRule) rule;
				printStartElement(th, URI, "cssStyleRule");
				{
					printStartElement(th, URI, "selectorText");
					printCharacters(th, styleRule.getSelectorText());
//					// e.g, split body, div, dl, dt, dd, ul, ol, li, h1, h2, h3, h4, h5, h6, pre, code, form, fieldset, legend, input, textarea, p, blockquote, th, td, address
//					StringTokenizer st = new StringTokenizer(styleRule.getSelectorText(), ",");
//					while(st.hasMoreTokens()) {
//						String text = st.nextToken();
//						printStartElement(th, URI, "sel");
//						printCharacters(th, text);
//						printEndElement(th, URI, "sel");
//					}
					printEndElement(th, URI, "selectorText");

					CSSStyleDeclaration style = styleRule.getStyle();
					printStartElement(th, URI, "style");
					for (int j = 0; j < style.getLength(); j++) {
						String property = style.item(j);
						
						printStartElement(th, URI, "property");
						printCharacters(th, property);
						printEndElement(th, URI, "property");

						printStartElement(th, URI, "cssValue");
						CSSValue cssValue = style.getPropertyCSSValue(property);
						printCSSValue(th, cssValue);
						printEndElement(th, URI, "cssValue");

						String priority = style.getPropertyPriority(property);
						if (priority != null && priority.length() > 0) {
							printStartElement(th, URI, "priority");
							printCharacters(th, priority);
							printEndElement(th, URI, "priority");
						}
					}
					printEndElement(th, URI, "style");
				}
				printEndElement(th, URI, "cssStyleRule");
				break;
			case CSSRule.CHARSET_RULE:
				CSSCharsetRule charsetRule = (CSSCharsetRule) rule;
				printStartElement(th, URI, "cssCharsetRule");
				printCharacters(th, charsetRule.getEncoding());
				printEndElement(th, URI, "cssCharsetRule");
				break;
			case CSSRule.FONT_FACE_RULE:
				CSSFontFaceRule fontFaceRule = (CSSFontFaceRule) rule;
				printStartElement(th, URI, "cssFontFaceRule");
				System.err
						.println("Unsupported ruleType = " + "FONT_FACE_RULE");
				printEndElement(th, URI, "cssFontFaceRule");
				break;
			case CSSRule.IMPORT_RULE:
				CSSImportRule importRule = (CSSImportRule) rule;
				printStartElement(th, URI, "cssImportRule");
				{
					printStartElement(th, URI, "href");
					printCharacters(th, importRule.getHref());
					printEndElement(th, URI, "href");
				}
				
				generateMediaList(importRule.getMedia(), th);
				
				printEndElement(th, URI, "cssImportRule");
				break;
			case CSSRule.MEDIA_RULE:
				CSSMediaRule mediaRule = (CSSMediaRule) rule;
				// System.err.println(mediaRule.getMedia());
				// System.err.println(mediaRule.getCssRules());
				printStartElement(th, URI, "cssMediaRule");
				
				generateMediaList(mediaRule.getMedia(), th);
					
				CSSRuleList rl = mediaRule.getCssRules();
				{
					generateCSSRuleList(rl, th);
				}
				printEndElement(th, URI, "cssMediaRule");
				break;
			case CSSRule.PAGE_RULE:
				CSSPageRule pageRule = (CSSPageRule) rule;
				printStartElement(th, URI, "cssPageRule");
				System.err.println("Unsupported ruleType = " + "PAGE_RULE");
				printEndElement(th, URI, "cssPageRule");
				break;
			case CSSRule.UNKNOWN_RULE:
				CSSUnknownRule unknownRule = (CSSUnknownRule) rule;
				printStartElement(th, URI, "cssUnknownRule");
				System.err.println("Unsupported ruleType = " + "UNKNOWN_RULE");
				printEndElement(th, URI, "cssUnknownRule");
				break;
			default:
				throw new RuntimeException("Unexptected RuleType: "
						+ rule.getType());
			}
		}
	}
	
	protected void printStartElement(ContentHandler th, String uri, String localName) throws SAXException {
		th.startElement(uri, localName, localName, attsEmpty);
	}
	
	protected void printEndElement(ContentHandler th, String uri, String localName) throws SAXException {
		th.endElement(uri, localName, localName);
	}
	
	protected void printCharacters(ContentHandler th, Object chars) throws SAXException {
		char[] ch = chars.toString().toCharArray();
		th.characters(ch, 0, ch.length);
	}

	protected void printCSSValue(ContentHandler th, CSSValue cssValue) throws SAXException {
		if (cssValue.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) {
			printStartElement(th, URI, "cssPrimitiveValue");
			CSSPrimitiveValue primValue = (CSSPrimitiveValue) cssValue;
			printCSSPrimitiveValue(th, primValue);
			printEndElement(th, URI, "cssPrimitiveValue");
		} else if (cssValue.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
			CSSValueList valueList = (CSSValueList) cssValue;
			
			printStartElement(th, URI, "cssValueList");
			for (int k = 0; k < valueList.getLength(); k++) {
				CSSValue cssValueK = valueList.item(k);
				printCSSValue(th, cssValueK);
			}
			printEndElement(th, URI, "cssValueList");
		} else if (cssValue.getCssValueType() == CSSValue.CSS_INHERIT) {
			printStartElement(th, URI, "cssInherit");
			printEndElement(th, URI, "cssInherit");
		} else {
			throw new RuntimeException("Unexptected CssValueType: "
					+ cssValue.getCssValueType());
		}
	}



	protected void printCSSPrimitiveValue(ContentHandler th,
			CSSPrimitiveValue primValue) throws DOMException, SAXException {
		
		short primitiveType = primValue.getPrimitiveType();
		printStartElement(th, URI, primitiveTagNames[primitiveType]);
		
		switch (primitiveType) {
		case CSSPrimitiveValue.CSS_NUMBER:
		case CSSPrimitiveValue.CSS_PERCENTAGE:
		case CSSPrimitiveValue.CSS_EMS:
		case CSSPrimitiveValue.CSS_EXS:
		case CSSPrimitiveValue.CSS_PX:
		case CSSPrimitiveValue.CSS_CM:
		case CSSPrimitiveValue.CSS_MM:
		case CSSPrimitiveValue.CSS_IN:
		case CSSPrimitiveValue.CSS_PT:
		case CSSPrimitiveValue.CSS_PC:
		case CSSPrimitiveValue.CSS_DEG:
		case CSSPrimitiveValue.CSS_RAD:
		case CSSPrimitiveValue.CSS_GRAD:
		case CSSPrimitiveValue.CSS_MS:
		case CSSPrimitiveValue.CSS_S:
		case CSSPrimitiveValue.CSS_HZ:
		case CSSPrimitiveValue.CSS_KHZ:
		case CSSPrimitiveValue.CSS_DIMENSION:
			// float value
			printCharacters(th, primValue.getFloatValue(primitiveType));
			break;
		case CSSPrimitiveValue.CSS_UNKNOWN:
		case CSSPrimitiveValue.CSS_STRING:
		case CSSPrimitiveValue.CSS_URI:
		case CSSPrimitiveValue.CSS_IDENT:
		case CSSPrimitiveValue.CSS_ATTR:
			// string value
			try {
				printCharacters(th, primValue.getStringValue());
			} catch (Exception e) {
				// Note: in some strange cases getStringValue() does not work even if primitiveType is string
				printCharacters(th, primValue.toString());
				// IF there is an issue the exception is thrown once again
			}
			break;
		case CSSPrimitiveValue.CSS_RGBCOLOR:
			RGBColor rgbColor = primValue.getRGBColorValue();
			{
				printStartElement(th, URI, "r");
				printCharacters(th, rgbColor.getRed());
				printEndElement(th, URI, "r");
				
				printStartElement(th, URI, "g");
				printCharacters(th, rgbColor.getGreen());
				printEndElement(th, URI, "g");
			
				printStartElement(th, URI, "b");
				printCharacters(th, rgbColor.getBlue());
				printEndElement(th, URI, "b");
			}
			break;
		default:
			throw new RuntimeException("Unexptected PrimitiveType: "
					+ primitiveType);
		}

		printEndElement(th, URI, primitiveTagNames[primitiveType]);
	}

}