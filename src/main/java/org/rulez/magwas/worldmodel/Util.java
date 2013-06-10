package org.rulez.magwas.worldmodel;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;


public final class Util {

	private static SessionFactory sessionFactory =   null;

	private static DocumentBuilder dBuilder = null;
	private static Logger logger = null;
	
	private Util() {
		//singleton
	}

  //Every method of initializing hibernate is deprecated AFAIK.
	@SuppressWarnings("deprecation")
	public static Session getSession(){
		if (sessionFactory == null) {
			sessionFactory =   new Configuration().configure().buildSessionFactory();
		}
		return sessionFactory.openSession();
	}
	
	public static void debug(String what) {
		log(Level.DEBUG,what);
	}

	private static String codes = "0123456789abcdef";
	public static void hexdump(String label,String str) {
		char[] arr = str.toCharArray();
		
		StringBuffer buff = new StringBuffer();
		for(int i=0;i<arr.length;i++) {
			//yeah, hexdump is magical, thus uses magic numbers
			buff.append(codes.charAt((arr[i]>>8)&0x0f))
				.append(codes.charAt((arr[i]>>4)&0x0f))
				.append(codes.charAt(arr[i]&0xf))
				.append(arr[i])
				.append(" ");
		}
		Util.debug(label + buff.toString());
	}

	public static void warning(String what) {
		log(Level.WARN,what);
	}

	public static void log(Level level, String what) {
		if(logger == null) {
			logger = Logger.getLogger(BaseObject.class);
		}
		logger.log(level, what);
	}

	public static void logException(Exception e) {
		log(Level.ERROR,"Exception: "+ e.getMessage());
		log(Level.INFO, "Stack trace: "+ ExceptionUtils.getStackTrace(e));
	}
	
	public static void shutdown() {
    	// Close caches and connection pools
    	sessionFactory.close();
    }

	
	private static DocumentBuilder getDocumentBuilder() {
		if (dBuilder == null) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			try {
				dBuilder = dbFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// FIXME log and die
				return null;
			}
		}
		return dBuilder;
	}

	public static Document newDocument() {
		dBuilder = getDocumentBuilder();
		Document doc = dBuilder.newDocument();
		ProcessingInstruction pi = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"stylesheet.xsl\"");
		doc.appendChild(pi);
		return doc;

	}

	public static Document newDocument(String str) throws SAXException, IOException {
		byte[] arr = str.getBytes("UTF-8");
		ByteArrayInputStream bis = new ByteArrayInputStream(arr);
		return newDocument(bis);
	}
	
	public static Document newDocument(ByteArrayInputStream bis) throws SAXException, IOException {
		dBuilder = getDocumentBuilder();
		return dBuilder.parse(bis);
	}

	public static String dom2String(Document doc) throws TransformerException {
		DOMSource domSource = new DOMSource(doc);
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		StringWriter out = new StringWriter();
		transformer.transform(domSource, new StreamResult(out));
		return out.toString();
	}

	public static String baseObject2String(BaseObject obj) {
		// maybe create an interface
		
		Document doc = Util.newDocument();
		Element rootnode = doc.createElement("objects");
		doc.appendChild(rootnode);
		Element xml;
		if (obj == null) {
			xml = doc.createElement("null");
		} else {
			xml = obj.toXML(doc);
		}
		rootnode.appendChild(xml);
		try {
			return dom2String(doc);
		} catch (TransformerException e) {
			logException(e);
			return "<exception>Transformation failed</exception>";
		}
	}

}

