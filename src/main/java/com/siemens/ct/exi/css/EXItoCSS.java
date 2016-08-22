package com.siemens.ct.exi.css;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamDecoder;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.values.FloatValue;
import com.siemens.ct.exi.values.ListValue;
import com.siemens.ct.exi.values.Value;
import com.siemens.ct.exi.values.ValueType;

public class EXItoCSS {

	public static void main(String[] args) throws FileNotFoundException, EXIException, IOException {
		if(args == null || args.length != 1) {
			System.out.println("Usage: <executable> input.css.xml.exi");
		} else {
			EXItoCSS exi2Css = new EXItoCSS();
			String sEXI = args[0];
			String sCSS = sEXI + CSSConstants.SUFFIX_CSS;
			
			exi2Css.generate(sEXI, CSSConstants.EXI_FACTORY, sCSS);
		}
	}
	
	
	public void generate(String fEXI, EXIFactory exiFactory, String fCSS) throws EXIException, FileNotFoundException, IOException {
		EXIStreamDecoder streamDecoder = exiFactory.createEXIStreamDecoder();
		EXIBodyDecoder bodyDecoder = streamDecoder.decodeHeader(new FileInputStream(fEXI));
		
		EventType eventType;
		
		StringBuilder sb = new StringBuilder();
		
//		boolean inMediaRule= false;
		boolean inImportRule= false;
		
		while ((eventType = bodyDecoder.next()) != null) {
			switch (eventType) {
			/* DOCUMENT */
			case START_DOCUMENT:
				bodyDecoder.decodeStartDocument();
				break;
			case END_DOCUMENT:
				bodyDecoder.decodeEndDocument();
				break;
			case START_ELEMENT:
			case START_ELEMENT_NS:
			case START_ELEMENT_GENERIC:
			case START_ELEMENT_GENERIC_UNDECLARED:
				// defer start element and keep on processing
				QNameContext se = bodyDecoder.decodeStartElement();
				if(CSSConstants.CSS_RGBCOLOR.equals(se.getLocalName())) {
					sb.append("rgb("); // HEX values ??
				} else if(CSSConstants.CSS_STRING.equals(se.getLocalName())) {
					sb.append("\"");
				} else if(CSSConstants.CSS_URI.equals(se.getLocalName())) {
					sb.append("url('");
				} else if(CSSConstants.CSS_CHARSET_RULE.equals(se.getLocalName())) {
					sb.append("@charset \"");
				} else if(CSSConstants.CSS_IMPORT_RULE.equals(se.getLocalName())) {
					sb.append("@import ");
					inImportRule = true;
				} else if(CSSConstants.CSS_MEDIA_RULE.equals(se.getLocalName())) {
					sb.append("@media ");
//					inMediaRule = true;
				} else if(CSSConstants.CSS_PAGE_RULE.equals(se.getLocalName())) {
					sb.append("@page "); // {
				} else if(CSSConstants.CSS_FONT_FACE_RULE.equals(se.getLocalName())) {
					sb.append("@font-face {");
				} else if(CSSConstants.CSS_COUNTER.equals(se.getLocalName())) {
					sb.append(" counter(");
				} else if(CSSConstants.CSS_RECT.equals(se.getLocalName())) {
					sb.append(" rect(");
				} else if(CSSConstants.LIST_STYLE.equals(se.getLocalName())) {
					sb.append(", ");
				} else if(CSSConstants.SEPARATOR.equals(se.getLocalName())) {
					sb.append(", ");
				} else if(CSSConstants.HREF.equals(se.getLocalName())) {
					sb.append("url(\"");
				} else if(CSSConstants.PRIORITY.equals(se.getLocalName())) {
					sb.setCharAt(sb.length()-1, ' '); // back one character, remove ;
					sb.append("!");
				} else if(CSSConstants.CSS_STYLE_RULE.equals(se.getLocalName())) {
//					if(inMediaRule) {
//						sb.append("{");
//					}
				}
				break;
			case END_ELEMENT:
			case END_ELEMENT_UNDECLARED:
				// String eeQNameAsString = bodyDecoder.getElementQNameAsString();
				QNameContext eeQName = bodyDecoder.decodeEndElement();
				if(CSSConstants.SELECTOR_TEXT.equals(eeQName.getLocalName())) {
					sb.append("{");
				} else if(CSSConstants.STYLE.equals(eeQName.getLocalName())) {
					sb.append("}");
				} else if(CSSConstants.PROPERTY.equals(eeQName.getLocalName())) {
					sb.append(":");
				} else if(CSSConstants.CSS_PRIMITIVE_VALUE.equals(eeQName.getLocalName())) {
					sb.append(" "); // TODO only space or also comma??? cssValueList
				} else if(CSSConstants.CSS_VALUE.equals(eeQName.getLocalName())) {
					sb.append(";");
				} else if(CSSConstants.CSS_PERCENTAGE.equals(eeQName.getLocalName())) {
					sb.append("%");
				} else if(CSSConstants.CSS_EMS.equals(eeQName.getLocalName())) {
					sb.append("em");
				} else if(CSSConstants.CSS_EXS.equals(eeQName.getLocalName())) {
					sb.append("ex");
				} else if(CSSConstants.CSS_PX.equals(eeQName.getLocalName())) {
					sb.append("px");
				} else if(CSSConstants.CSS_CM.equals(eeQName.getLocalName())) {
					sb.append("cm");
				} else if(CSSConstants.CSS_MM.equals(eeQName.getLocalName())) {
					sb.append("mm");
				} else if(CSSConstants.CSS_IN.equals(eeQName.getLocalName())) {
					sb.append("in");
				} else if(CSSConstants.CSS_PT.equals(eeQName.getLocalName())) {
					sb.append("pt");
				} else if(CSSConstants.CSS_PC.equals(eeQName.getLocalName())) {
					sb.append("pc");
				} else if(CSSConstants.CSS_DEG.equals(eeQName.getLocalName())) {
					sb.append("deg");
				} else if(CSSConstants.CSS_RAD.equals(eeQName.getLocalName())) {
					sb.append("rad");
				} else if(CSSConstants.CSS_GRAD.equals(eeQName.getLocalName())) {
					sb.append("grad");
				} else if(CSSConstants.CSS_MS.equals(eeQName.getLocalName())) {
					sb.append("ms");
				} else if(CSSConstants.CSS_S.equals(eeQName.getLocalName())) {
					sb.append("s");
				} else if(CSSConstants.CSS_HZ.equals(eeQName.getLocalName())) {
					sb.append("hz");
				} else if(CSSConstants.CSS_KHZ.equals(eeQName.getLocalName())) {
					sb.append("khz");
				} else if(CSSConstants.CSS_STRING.equals(eeQName.getLocalName())) {
					// eg., content: "\A"; for end of line
					sb.append("\"");
				} else if(CSSConstants.CSS_INHERIT.equals(eeQName.getLocalName())) {
					sb.append("inherit");
				} else if(CSSConstants.CSS_CHARSET_RULE.equals(eeQName.getLocalName())) {
					sb.append("\";");
				} else if(CSSConstants.CSS_IMPORT_RULE.equals(eeQName.getLocalName())) {
					sb.append(";");
					inImportRule = false;
				} else if(CSSConstants.HREF.equals(eeQName.getLocalName())) {
					sb.append("\") ");
				} else if(CSSConstants.CSS_RGBCOLOR.equals(eeQName.getLocalName())) {
					sb.append(")");
				} else if(CSSConstants.R.equals(eeQName.getLocalName())) {
					sb.append(",");
				} else if(CSSConstants.G.equals(eeQName.getLocalName())) {
					sb.append(",");
				} else if(CSSConstants.PRIORITY.equals(eeQName.getLocalName())) {
					sb.append(";");
				} else if(CSSConstants.TOP.equals(eeQName.getLocalName()) || CSSConstants.RIGHT.equals(eeQName.getLocalName()) || CSSConstants.BOTTOM.equals(eeQName.getLocalName())) {
					sb.append(",");
				} else if(CSSConstants.CSS_URI.equals(eeQName.getLocalName())) {
					sb.append("')");
				} else if(CSSConstants.MEDIA_LIST.equals(eeQName.getLocalName())) {
					// sb.append("{");
					if(!inImportRule) {
						sb.append("{");	
					}
				} else if(CSSConstants.CSS_MEDIA_RULE.equals(eeQName.getLocalName())) {
					sb.append("}");
//					inMediaRule = false;
				} else if(CSSConstants.CSS_PAGE_RULE.equals(eeQName.getLocalName())) {
					// sb.append("}");
				} else if(CSSConstants.CSS_FONT_FACE_RULE.equals(eeQName.getLocalName())) {
					// sb.append("}");
				} else if(CSSConstants.CSS_STYLE_RULE.equals(eeQName.getLocalName())) {
//					if(inMediaRule) {
//						sb.append("}");
//					}
				} else if(CSSConstants.CSS_COUNTER.equals(eeQName.getLocalName())) {
					sb.append(" ) ");	
				} else if(CSSConstants.CSS_RECT.equals(eeQName.getLocalName())) {
					sb.append(" ) ");	
				}
				break;
			case CHARACTERS:
			case CHARACTERS_GENERIC:
			case CHARACTERS_GENERIC_UNDECLARED:
				Value val = bodyDecoder.decodeCharacters();
				if(val.getValueType() == ValueType.FLOAT) {
					FloatValue fv = (FloatValue) val;
					long m = fv.getMantissa().longValue();
					long e = fv.getExponent().longValue();
					if(e == 0) {
						sb.append(m + "");
					} else if (e > 0) {
						double fm = m;
						while(e != 0) {
							fm = fm * 10;
							e--;
						}
						sb.append(roundToDecimals(fm, 2) + "");
					} else {
						assert(e < 0);
						double fm = m;
						while(e != 0) {
							fm = fm / 10;
							e++;
						}
						sb.append(roundToDecimals(fm, 2) + "");
					}
				} else if(val.getValueType() == ValueType.LIST) {
					// Note: selectorText --> separator is comma
					ListValue lv = (ListValue) val;
					if(lv.getNumberOfValues() > 0) {
						Value[] vals = lv.toValues();
						for(int i=0; i<(vals.length-1); i++) {
							sb.append(vals[i].toString());
							sb.append(',');
						}
						sb.append(vals[vals.length-1].toString());
					}
				} else {
					// TODO more efficiently for list et cetera
					String sch = val.toString();
					// TODO special characters: e.g, content: "\A"; for end of line
					// sch = sch.replace("/(?:\r\n|\r|\n)/g", "<br />");
					sch = sch.replace("\n", "\\A");
					sch = sch.replace("\r", "\\A");
					sch = sch.replace("\r\n", "\\A");
					
					sb.append(sch);
				}
				break;
			default:
				throw new RuntimeException("Unsupported eventType " + eventType);
				// break;
			}
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(fCSS));
		writer.write(sb.toString());
		writer.close();
		
//		PrintStream ps = new PrintStream(fCSS);
//		ps.close();
		
		
//		CSSStyleSheetImpl ss = new CSSStyleSheetImpl();
//		int i = ss.insertRule("rule", 0);
		
	}
	
	public static double roundToDecimals(double d, int c)  
	{   
	   int temp = (int)(d * Math.pow(10 , c));  
	   return ((double)temp)/Math.pow(10 , c);  
	}

}
