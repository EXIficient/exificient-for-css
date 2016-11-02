package com.siemens.ct.exi.css;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

import javax.xml.transform.TransformerConfigurationException;

import junit.framework.TestCase;

import org.junit.Test;
import org.meteogroup.jbrotli.Brotli;
import org.meteogroup.jbrotli.BrotliCompressor;
import org.meteogroup.jbrotli.BrotliStreamCompressor;
import org.meteogroup.jbrotli.libloader.BrotliLibraryLoader;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleSheet;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import com.yahoo.platform.yui.compressor.CssCompressor;

public class CSStoEXITest extends TestCase {

	PrintStream ps = System.out;

	private static boolean setUpIsDone = false;

	public void setUp() {
		// executed only once, before the first test
		if (!setUpIsDone) {
			ps.println(System.getProperty("java.version"));
			ps.println("CSS-Name; CSS; YuiCompressed; EXI;"
//					+ " EXI_DTRM; "
					+ "EXI_Compr; EXIGzip; EXIBrotli; GZip; Brotli");
			setUpIsDone = true;
		}
	}

	public byte[] writeGzip(byte[] b2compress) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = new GZIPOutputStream(baos);
		gzos.write(b2compress);
		gzos.finish();
		gzos.close();

		return baos.toByteArray();
	}

	public byte[] writeBrotli(byte[] b2compress) throws IOException {
		byte[] brotliCompr;

		boolean loadedDll = false;
		try {
			BrotliLibraryLoader.loadBrotli();
			loadedDll = true;
		} catch (UnsatisfiedLinkError e) {
			// e.printStackTrace();
		} catch (IllegalStateException e) {
			// e.printStackTrace();
		} catch (SecurityException e) {
			// e.printStackTrace();
		}
		if (loadedDll) {
			boolean defaultDictionary = true;
			if (defaultDictionary) {
				// byte[] inBuf =
				// "Brotli: a new compression algorithm for the internet. Now available for Java!".getBytes();
				boolean doFlush = true;
				BrotliStreamCompressor streamCompressor = new BrotliStreamCompressor(
						Brotli.DEFAULT_PARAMETER);
				brotliCompr = streamCompressor.compressArray(b2compress,
						doFlush);
				streamCompressor.close();
			} else {
				byte[] compressedBuf = new byte[b2compress.length];
				BrotliCompressor compressor = new BrotliCompressor();
				int outLength = compressor.compress(Brotli.DEFAULT_PARAMETER,
						b2compress, compressedBuf);
				brotliCompr = Arrays.copyOfRange(b2compress, 0, outLength);
			}
		} else {
			// DLL loading error
			brotliCompr = new byte[0];
		}

		return brotliCompr;
	}

	public static byte[] readBytes(InputStream inputStream) throws IOException {
		byte[] b = new byte[1024];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int c;
		while ((c = inputStream.read(b)) != -1) {
			os.write(b, 0, c);
		}
		return os.toByteArray();
	}

	public static String generateEXI4CSS(String css, EXIFactory ef)
			throws IOException, TransformerConfigurationException,
			EXIException, SAXException {
		CSStoEXI css2Exi = new CSStoEXI();
		String sXML = File.createTempFile("css", "xml").getAbsolutePath();
		String sEXI = File.createTempFile("css", "exi").getAbsolutePath();
		css2Exi.generate(css, ef, sXML, sEXI);

		return sEXI;
	}

	protected void _test(String css) throws IOException, EXIException,
			SAXException, TransformerConfigurationException {
		// EXI for CSS
		String sEXI = generateEXI4CSS(css, CSSConstants.EXI_FACTORY);
		// String sEXIDtrm = generateEXI4CSS(css, CSSConstants.EXI_FACTORY_DTRM);
		String sEXICompr = generateEXI4CSS(css,
				CSSConstants.EXI_FACTORY_COMPRESSION);
		String sEXIPreCompr = generateEXI4CSS(css,
				CSSConstants.EXI_FACTORY_PRE_COMPRESSION);
		byte[] sEXIGzipBytes = writeGzip(readBytes(new FileInputStream(
				sEXIPreCompr)));
		byte[] sEXIBrotliBytes = writeBrotli(readBytes(new FileInputStream(
				sEXIPreCompr)));
		// Yui Compressor
		Reader cssIn = new FileReader(new File(css));
		CssCompressor cssCompressor = new CssCompressor(cssIn);
		File fCssOut = File.createTempFile("cssCompressor", "css");
		Writer cssOut = new FileWriter(fCssOut);
		cssCompressor.compress(cssOut, -1);
		cssOut.flush();
		// GZip
		byte[] b2compress = readBytes(new FileInputStream(css));
		byte[] gzipCompr = writeGzip(b2compress);
		// Brotli
		byte[] brotliCompr = writeBrotli(b2compress);
		//
		System.out.println(css + "; " + new File(css).length() + "; "
				+ fCssOut.length() + "; " + new File(sEXI).length() + "; "
//				+ new File(sEXIDtrm).length() + "; "
				+ new File(sEXICompr).length() + "; " + sEXIGzipBytes.length
				+ "; " + sEXIBrotliBytes.length + "; " + gzipCompr.length
				+ "; " + brotliCompr.length);

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
	public void testDesign() throws FileNotFoundException, IOException,
			EXIException, SAXException, TransformerConfigurationException {
		_test("data/design.css");
	}

	@Test
	public void testModule() throws FileNotFoundException, IOException,
			EXIException, SAXException, TransformerConfigurationException {
		_test("data/module.css");
	}

	@Test
	public void testCSS2_1() throws FileNotFoundException, IOException,
			EXIException, SAXException, TransformerConfigurationException {
		_test("data/css2_1.css");
	}

	@Test
	public void testCSS3_1() throws FileNotFoundException, IOException,
			EXIException, SAXException, TransformerConfigurationException {
		_test("data/css3_1.css");
	}

	@Test
	public void testW3C() throws FileNotFoundException, IOException,
			EXIException, SAXException, TransformerConfigurationException {
		_test("data/w3c.css");
	}

	@Test
	public void testRules() throws FileNotFoundException, IOException,
			EXIException, SAXException, TransformerConfigurationException {
		_test("data/rules.css");
	}

	@Test
	public void testAmazon() throws FileNotFoundException, IOException,
			EXIException, SAXException, TransformerConfigurationException {
		_test("data/amazon.css");
	}

	@Test
	// Bank of America
	public void testBoa() throws FileNotFoundException, IOException,
			EXIException, SAXException, TransformerConfigurationException {
		_test("data/boa.css");
	}

	@Test
	public void testCapitalone() throws FileNotFoundException, IOException,
			EXIException, SAXException, TransformerConfigurationException {
		_test("data/capitalone.css");
	}

	@Test
	public void testFujitsu() throws FileNotFoundException, IOException,
			EXIException, SAXException, TransformerConfigurationException {
		_test("data/fujitsu.css");
	}

	@Test
	// Wall Street Journal
	public void testWSJ() throws FileNotFoundException, IOException,
			EXIException, SAXException, TransformerConfigurationException {
		_test("data/wsj.css");
	}

	@Test
	// Wall Street Journal
	public void testYahooJapan() throws FileNotFoundException, IOException,
			EXIException, SAXException, TransformerConfigurationException {
		_test("data/yahoo_japan.css");
	}

}
