package com.siemens.ct.exi.css;

import java.io.InputStream;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.event.Characters;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTagGrammar;
import com.siemens.ct.exi.grammars.production.Production;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class CSSConstants {
	
	public static final String XSD_LOCATION = "/exi4css.xsd";
	public static Grammars EXI_FOR_CSS_GRAMMARS;
	public static EXIFactory EXI_FACTORY;
//	public static EXIFactory EXI_FACTORY_DTRM;
	public static EXIFactory EXI_FACTORY_COMPRESSION;
	public static EXIFactory EXI_FACTORY_PRE_COMPRESSION;
	
	static {
		try {
			InputStream isXsd = CSSConstants.class.getResourceAsStream(CSSConstants.XSD_LOCATION);
			EXI_FOR_CSS_GRAMMARS = GrammarFactory.newInstance().createGrammars(isXsd);
			
			EXI_FACTORY = DefaultEXIFactory.newInstance();
			EXI_FACTORY.setFidelityOptions(FidelityOptions.createStrict());
			EXI_FACTORY.setGrammars(CSSConstants.EXI_FOR_CSS_GRAMMARS); // use XML schema
			
//			 QName[] dtrMapTypes = {new QName("", "propertyType")};
//			 QName qnCSS = new QName("urn:javascript", "cssProperty");
//			 QName[] dtrMapRepresentations = {qnCSS};
//			 QNameContext qnc = EXI_FOR_CSS_GRAMMARS.getGrammarContext().getGrammarUriContext("").getQNameContext("cssProperty");
//			 SchemaInformedFirstStartTagGrammar tg = qnc.getTypeGrammar();
//			 Production prod = tg.getProduction(EventType.CHARACTERS);
//			 Characters ch = (Characters) prod.getEvent();
//			 Datatype dt = ch.getDatatype();
			
//			QName[] dtrMapTypes = {new QName("http://www.w3.org/2001/XMLSchema", "decimal")};
//			QName[] dtrMapRepresentations = {new QName("http://www.w3.org/2009/exi", "string")};


//			EXI_FACTORY.setDatatypeRepresentationMap(dtrMapTypes, dtrMapRepresentations);
//			 EXI_FACTORY.registerDatatypeRepresentationMapDatatype(qnCSS, dt);
			
			
//			EXI_FACTORY_DTRM = EXI_FACTORY;
//			EXI_FACTORY_DTRM = DefaultEXIFactory.newInstance();
//			// Note: do not use strict if you still want to allow unknown property names such as "-moz-user-select"
//			EXI_FACTORY_DTRM.setFidelityOptions(FidelityOptions.createStrict()); 
//			EXI_FACTORY_DTRM.setGrammars(CSSConstants.EXI_FOR_CSS_GRAMMARS); // use XML schema
//			QName[] dtrMapTypes = {new QName("", "propertyType")};
//			QName qnCSS = new QName("urn:javascript", "cssProperty");
//			QName[] dtrMapRepresentations = {qnCSS};
//			QNameContext qnc = EXI_FOR_CSS_GRAMMARS.getGrammarContext().getGrammarUriContext("").getQNameContext("cssProperty");
//			SchemaInformedFirstStartTagGrammar tg = qnc.getTypeGrammar();
//			Production prod = tg.getProduction(EventType.CHARACTERS);
//			Characters ch = (Characters) prod.getEvent();
//			Datatype dt = ch.getDatatype();
//			EXI_FACTORY_DTRM.setDatatypeRepresentationMap(dtrMapTypes, dtrMapRepresentations);
//			EXI_FACTORY_DTRM.registerDatatypeRepresentationMapDatatype(qnCSS, dt);
			
			EXI_FACTORY_COMPRESSION = DefaultEXIFactory.newInstance();
			EXI_FACTORY_COMPRESSION.setFidelityOptions(FidelityOptions.createStrict());
			EXI_FACTORY_COMPRESSION.setGrammars(CSSConstants.EXI_FOR_CSS_GRAMMARS); // use XML schema
			EXI_FACTORY_COMPRESSION.setCodingMode(CodingMode.COMPRESSION); // use deflate compression for larger XML files
			EXI_FACTORY_COMPRESSION.getEncodingOptions().setOption(EncodingOptions.DEFLATE_COMPRESSION_VALUE, java.util.zip.Deflater.BEST_COMPRESSION);
			
			EXI_FACTORY_PRE_COMPRESSION = DefaultEXIFactory.newInstance();
			EXI_FACTORY_PRE_COMPRESSION.setFidelityOptions(FidelityOptions.createStrict());
			EXI_FACTORY_PRE_COMPRESSION.setGrammars(CSSConstants.EXI_FOR_CSS_GRAMMARS); // use XML schema
			EXI_FACTORY_PRE_COMPRESSION.setCodingMode(CodingMode.PRE_COMPRESSION); // use pre-compression for following generic compression
			
		} catch (EXIException e) {
			System.err.println("Not able to load EXI grammars from " + XSD_LOCATION);
		}
	}
	
	
	public static final String SUFFIX_XML = ".xml";
	public static final String SUFFIX_EXI = SUFFIX_XML + ".exi";
	public static final String SUFFIX_CSS = ".css";

	static final String URI = "";
	
	/* misc */
	public static final String STYLESHEET = "stylesheet";
	public static final String MEDIA_LIST = "mediaList";
	public static final String STYLE = "style";
	public static final String PROPERTY = "property";
	public static final String CSS_VALUE = "cssValue";
	public static final String PRIORITY = "priority";
	public static final String SELECTOR_TEXT = "selectorText";
	public static final String CSS_STYLE_RULE = "cssStyleRule";
	public static final String CSS_CHARSET_RULE = "cssCharsetRule";
	public static final String CSS_FONT_FACE_RULE = "cssFontFaceRule";
	public static final String CSS_IMPORT_RULE = "cssImportRule";
	public static final String HREF = "href";
	public static final String CSS_MEDIA_RULE = "cssMediaRule";
	public static final String CSS_PAGE_RULE = "cssPageRule";
	public static final String CSS_UNKNOWN_RULE = "cssUnknownRule";
	public static final String CSS_PRIMITIVE_VALUE = "cssPrimitiveValue";
	public static final String CSS_VALUE_LIST = "cssValueList";
	public static final String CSS_INHERIT = "cssInherit";
	public static final String R = "r";
	public static final String G = "g";
	public static final String B = "b";
	public static final String IDENTIFIER = "identifier";
	public static final String LIST_STYLE = "listStyle";
	public static final String SEPARATOR = "separator";
	public static final String TOP = "top";
	public static final String RIGHT = "right";
	public static final String BOTTOM = "bottom";
	public static final String LEFT = "left";
	
	/* primitive Types */
	public static final String CSS_UNKNOWN = "cssUnknown";
	public static final String CSS_NUMBER = "cssNumber";
	public static final String CSS_PERCENTAGE = "cssPercentage";
	public static final String CSS_EMS = "cssEms";
	public static final String CSS_EXS = "cssExs";
	public static final String CSS_PX = "cssPx";
	public static final String CSS_CM = "cssCm";
	public static final String CSS_MM = "cssMm";
	public static final String CSS_IN = "cssIn";
	public static final String CSS_PT = "cssPt";
	public static final String CSS_PC = "cssPc";
	public static final String CSS_DEG = "cssDeg";
	public static final String CSS_RAD = "cssRad";
	public static final String CSS_GRAD = "cssGrad";
	public static final String CSS_MS = "cssMs";
	public static final String CSS_S = "cssS";
	public static final String CSS_HZ  = "cssHz";
	public static final String CSS_KHZ = "cssKhz";
	public static final String CSS_DIMENSION = "cssDimension";
	public static final String CSS_STRING = "cssString";
	public static final String CSS_URI = "cssUri";
	public static final String CSS_IDENT = "cssIdent";
	public static final String CSS_ATTR = "cssAttr";
	public static final String CSS_COUNTER = "cssCounter";
	public static final String CSS_RECT = "cssRect";
	public static final String CSS_RGBCOLOR = "cssRgbColor";
	
}
