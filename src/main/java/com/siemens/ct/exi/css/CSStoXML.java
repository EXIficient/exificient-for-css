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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.w3c.css.sac.InputSource;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSCharsetRule;
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSPageRule;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSUnknownRule;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;
import org.w3c.dom.stylesheets.MediaList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;

public class CSStoXML {
	
	
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
		CSSConstants.CSS_UNKNOWN,
		// public static final short CSS_NUMBER = 1;
		CSSConstants.CSS_NUMBER,
		// public static final short CSS_PERCENTAGE = 2;
		CSSConstants.CSS_PERCENTAGE,
		// public static final short CSS_EMS = 3;
		CSSConstants.CSS_EMS,
		// public static final short CSS_EXS = 4;
		CSSConstants.CSS_EXS,
		// public static final short CSS_PX = 5;
		CSSConstants.CSS_PX,
		// public static final short CSS_CM = 6;
		CSSConstants.CSS_CM,
		// public static final short CSS_MM = 7;
		CSSConstants.CSS_MM,
		// public static final short CSS_IN = 8;
		CSSConstants.CSS_IN,
		// public static final short CSS_PT = 9;
		CSSConstants.CSS_PT,
		// public static final short CSS_PC = 10;
		CSSConstants.CSS_PC,
		// public static final short CSS_DEG = 11;
		CSSConstants.CSS_DEG,
		// public static final short CSS_RAD = 12;
		CSSConstants.CSS_RAD,
		// public static final short CSS_GRAD = 13;
		CSSConstants.CSS_GRAD,
		// public static final short CSS_MS = 14;
		CSSConstants.CSS_MS,
		// public static final short CSS_S = 15;
		CSSConstants.CSS_S,
		// public static final short CSS_HZ = 16;
		CSSConstants.CSS_HZ,
		// public static final short CSS_KHZ = 17;
		CSSConstants.CSS_KHZ,
		// public static final short CSS_DIMENSION = 18;
		CSSConstants.CSS_DIMENSION,
		// public static final short CSS_STRING = 19;
		CSSConstants.CSS_STRING,
		// public static final short CSS_URI = 20;
		CSSConstants.CSS_URI,
		// public static final short CSS_IDENT = 21;
		CSSConstants.CSS_IDENT,
		// public static final short CSS_ATTR = 22;
		CSSConstants.CSS_ATTR,
		// public static final short CSS_COUNTER = 23;
		CSSConstants.CSS_COUNTER,
		// public static final short CSS_RECT = 24;
		CSSConstants.CSS_RECT,
		// public static final short CSS_RGBCOLOR = 25;
		CSSConstants.CSS_RGBCOLOR };

	public void generate(InputStream stream) throws IOException, TransformerConfigurationException, SAXException {
		PrintStream ps = System.out;
		generate(stream, ps);
	}

	public void generate(InputStream stream, PrintStream ps) throws IOException, TransformerConfigurationException, SAXException {
		InputSource source = new InputSource(new InputStreamReader(stream));
		CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
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
		printStartElement(th, CSSConstants.URI, CSSConstants.STYLESHEET);

		CSSRuleList ruleList = stylesheet.getCssRules();
		generateCSSRuleList(ruleList, th);

		printEndElement(th, CSSConstants.URI, CSSConstants.STYLESHEET);
		th.endDocument();
	}
	
	protected void generateMediaList(MediaList mediaList, TransformerHandler th) throws SAXException {
		printStartElement(th, CSSConstants.URI, CSSConstants.MEDIA_LIST);
		for(int k=0;k<mediaList.getLength(); k++) {
			printCharacters(th, mediaList.item(k));
		}
		printEndElement(th, CSSConstants.URI, CSSConstants.MEDIA_LIST);
	}
	
	protected void generateCSSStyleDeclaration(CSSStyleDeclaration style, TransformerHandler th) throws SAXException {
		printStartElement(th, CSSConstants.URI, CSSConstants.STYLE);
		for (int j = 0; j < style.getLength(); j++) {
			String property = style.item(j);
			
			printStartElement(th, CSSConstants.URI, CSSConstants.PROPERTY);
			printCharacters(th, property);
			printEndElement(th, CSSConstants.URI, CSSConstants.PROPERTY);

			printStartElement(th, CSSConstants.URI, CSSConstants.CSS_VALUE);
			CSSValue cssValue = style.getPropertyCSSValue(property);
			printCSSValue(th, cssValue);
			printEndElement(th, CSSConstants.URI, CSSConstants.CSS_VALUE);

			String priority = style.getPropertyPriority(property);
			if (priority != null && priority.length() > 0) {
				printStartElement(th, CSSConstants.URI, CSSConstants.PRIORITY);
				printCharacters(th, priority);
				printEndElement(th, CSSConstants.URI, CSSConstants.PRIORITY);
			}
		}
		printEndElement(th, CSSConstants.URI, CSSConstants.STYLE);
	}
	
	protected void generateSelectorText(String selectorText, TransformerHandler th) throws SAXException {
		printStartElement(th, CSSConstants.URI, CSSConstants.SELECTOR_TEXT);
		// remove commas --> list of strings
		// e.g, html, address, blockquote --> html address blockquote
		selectorText = selectorText.replace(",", "");
		printCharacters(th, selectorText);
//		// e.g, split body, div, dl, dt, dd, ul, ol, li, h1, h2, h3, h4, h5, h6, pre, code, form, fieldset, legend, input, textarea, p, blockquote, th, td, address
//		StringTokenizer st = new StringTokenizer(styleRule.getSelectorText(), ",");
//		while(st.hasMoreTokens()) {
//			String text = st.nextToken();
//			printStartElement(th, URI, "sel");
//			printCharacters(th, text);
//			printEndElement(th, URI, "sel");
//		}
		printEndElement(th, CSSConstants.URI, CSSConstants.SELECTOR_TEXT);
		
	}
	
	protected void generateCSSRuleList(CSSRuleList ruleList, TransformerHandler th) throws SAXException {
		for (int i = 0; i < ruleList.getLength(); i++) {
			CSSRule rule = ruleList.item(i);
			switch (rule.getType()) {
			case CSSRule.STYLE_RULE:
				CSSStyleRule styleRule = (CSSStyleRule) rule;
				printStartElement(th, CSSConstants.URI, CSSConstants.CSS_STYLE_RULE);
				{
					generateSelectorText(styleRule.getSelectorText(), th);
					generateCSSStyleDeclaration(styleRule.getStyle(), th);
				}
				printEndElement(th, CSSConstants.URI, CSSConstants.CSS_STYLE_RULE);
				break;
			case CSSRule.CHARSET_RULE:
				CSSCharsetRule charsetRule = (CSSCharsetRule) rule;
				printStartElement(th, CSSConstants.URI, CSSConstants.CSS_CHARSET_RULE);
				printCharacters(th, charsetRule.getEncoding());
				printEndElement(th, CSSConstants.URI, CSSConstants.CSS_CHARSET_RULE);
				break;
			case CSSRule.FONT_FACE_RULE:
				CSSFontFaceRule fontFaceRule = (CSSFontFaceRule) rule;
				printStartElement(th, CSSConstants.URI, CSSConstants.CSS_FONT_FACE_RULE);
				this.generateCSSStyleDeclaration(fontFaceRule.getStyle(), th);
				printEndElement(th, CSSConstants.URI, CSSConstants.CSS_FONT_FACE_RULE);
				break;
			case CSSRule.IMPORT_RULE:
				CSSImportRule importRule = (CSSImportRule) rule;
				printStartElement(th, CSSConstants.URI, CSSConstants.CSS_IMPORT_RULE);
				{
					printStartElement(th, CSSConstants.URI, CSSConstants.HREF);
					printCharacters(th, importRule.getHref());
					printEndElement(th, CSSConstants.URI, CSSConstants.HREF);
				}
				
				generateMediaList(importRule.getMedia(), th);
				
				printEndElement(th, CSSConstants.URI, CSSConstants.CSS_IMPORT_RULE);
				break;
			case CSSRule.MEDIA_RULE:
				CSSMediaRule mediaRule = (CSSMediaRule) rule;
				// System.err.println(mediaRule.getMedia());
				// System.err.println(mediaRule.getCssRules());
				printStartElement(th, CSSConstants.URI, CSSConstants.CSS_MEDIA_RULE);
				
				generateMediaList(mediaRule.getMedia(), th);
					
				CSSRuleList rl = mediaRule.getCssRules();
				{
					generateCSSRuleList(rl, th);
				}
				printEndElement(th, CSSConstants.URI, CSSConstants.CSS_MEDIA_RULE);
				break;
			case CSSRule.PAGE_RULE:
				CSSPageRule pageRule = (CSSPageRule) rule;
				printStartElement(th, CSSConstants.URI, CSSConstants.CSS_PAGE_RULE);
				generateSelectorText(pageRule.getSelectorText(), th);
				generateCSSStyleDeclaration(pageRule.getStyle(), th);
				printEndElement(th, CSSConstants.URI, CSSConstants.CSS_PAGE_RULE);
				break;
			case CSSRule.UNKNOWN_RULE:
				CSSUnknownRule unknownRule = (CSSUnknownRule) rule;
				printStartElement(th, CSSConstants.URI, CSSConstants.CSS_UNKNOWN_RULE);
				printCharacters(th, unknownRule);
				printEndElement(th, CSSConstants.URI, CSSConstants.CSS_UNKNOWN_RULE);
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
			printStartElement(th, CSSConstants.URI, CSSConstants.CSS_PRIMITIVE_VALUE);
			CSSPrimitiveValue primValue = (CSSPrimitiveValue) cssValue;
			printCSSPrimitiveValue(th, primValue);
			printEndElement(th, CSSConstants.URI, CSSConstants.CSS_PRIMITIVE_VALUE);
		} else if (cssValue.getCssValueType() == CSSValue.CSS_VALUE_LIST) {
			CSSValueList valueList = (CSSValueList) cssValue;
			
			printStartElement(th, CSSConstants.URI, CSSConstants.CSS_VALUE_LIST);
			for (int k = 0; k < valueList.getLength(); k++) {
				CSSValue cssValueK = valueList.item(k);
				printCSSValue(th, cssValueK);
			}
			printEndElement(th, CSSConstants.URI, CSSConstants.CSS_VALUE_LIST);
		} else if (cssValue.getCssValueType() == CSSValue.CSS_INHERIT) {
			printStartElement(th, CSSConstants.URI, CSSConstants.CSS_INHERIT);
			printEndElement(th, CSSConstants.URI, CSSConstants.CSS_INHERIT);
		} else {
			throw new RuntimeException("Unexptected CssValueType: "
					+ cssValue.getCssValueType());
		}
	}



	protected void printCSSPrimitiveValue(ContentHandler th,
			CSSPrimitiveValue primValue) throws DOMException, SAXException {
		
		short primitiveType = primValue.getPrimitiveType();
		printStartElement(th, CSSConstants.URI, primitiveTagNames[primitiveType]);
		
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
//			try {
				printCharacters(th, primValue.getStringValue());
//			} catch (Exception e) {
//				// Note: Snapshot version cssparser-0.9.20-20160721.090615-5.jar resolves issue
//				// Note: in some strange cases getStringValue() does not work even if primitiveType is string
//				printCharacters(th, primValue.toString());
//				// IF there is an issue the exception is thrown once again
//			}
			break;
		case CSSPrimitiveValue.CSS_COUNTER:
			Counter counter = primValue.getCounterValue();
			{
				printStartElement(th, CSSConstants.URI, CSSConstants.IDENTIFIER);
				printCharacters(th, counter.getIdentifier());
				printEndElement(th, CSSConstants.URI, CSSConstants.IDENTIFIER);
				
				if(counter.getListStyle() != null && counter.getListStyle().length()> 0) {
					printStartElement(th, CSSConstants.URI, CSSConstants.LIST_STYLE);
					printCharacters(th, counter.getListStyle());
					printEndElement(th, CSSConstants.URI, CSSConstants.LIST_STYLE);					
				}

				if(counter.getSeparator() != null && counter.getSeparator().length()> 0) {
					printStartElement(th, CSSConstants.URI, CSSConstants.SEPARATOR);
					printCharacters(th, counter.getSeparator());
					printEndElement(th, CSSConstants.URI, CSSConstants.SEPARATOR);					
				}
			}
			break;
		case CSSPrimitiveValue.CSS_RECT:
			Rect rect = primValue.getRectValue();
			{
				printStartElement(th, CSSConstants.URI, CSSConstants.TOP);
				printCSSPrimitiveValue(th, rect.getTop());
				printEndElement(th, CSSConstants.URI, CSSConstants.TOP);
				
				printStartElement(th, CSSConstants.URI, CSSConstants.RIGHT);
				printCSSPrimitiveValue(th, rect.getRight());
				printEndElement(th, CSSConstants.URI, CSSConstants.RIGHT);
				
				printStartElement(th, CSSConstants.URI, CSSConstants.BOTTOM);
				printCSSPrimitiveValue(th, rect.getBottom());
				printEndElement(th, CSSConstants.URI, CSSConstants.BOTTOM);
				
				printStartElement(th, CSSConstants.URI, CSSConstants.LEFT);
				printCSSPrimitiveValue(th, rect.getLeft());
				printEndElement(th, CSSConstants.URI, CSSConstants.LEFT);
			}
			 break;
		case CSSPrimitiveValue.CSS_RGBCOLOR:
			RGBColor rgbColor = primValue.getRGBColorValue();
			{
				printStartElement(th, CSSConstants.URI, CSSConstants.R);
				printCharacters(th, rgbColor.getRed());
				printEndElement(th, CSSConstants.URI, CSSConstants.R);
				
				printStartElement(th, CSSConstants.URI, CSSConstants.G);
				printCharacters(th, rgbColor.getGreen());
				printEndElement(th, CSSConstants.URI, CSSConstants.G);
			
				printStartElement(th, CSSConstants.URI, CSSConstants.B);
				printCharacters(th, rgbColor.getBlue());
				printEndElement(th, CSSConstants.URI, CSSConstants.B);
			}
			break;
		default:
			throw new RuntimeException("Unexptected PrimitiveType: "
					+ primitiveType);
		}

		printEndElement(th, CSSConstants.URI, primitiveTagNames[primitiveType]);
	}

}