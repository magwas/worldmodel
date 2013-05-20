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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;


public class Util {

	private static SessionFactory sessionFactory =   null;

	private static DocumentBuilder dBuilder = null;
	private static Logger logger = null;
	
	private Util() {
		//singleton
	}

	@SuppressWarnings("deprecation")//Every method of initializing hibernate is deprecated AFAIK.
	public static Session getSession(){
		if (sessionFactory == null) {
			sessionFactory =   new Configuration().configure().buildSessionFactory();
		}
		Session session = sessionFactory.openSession();
		return session;
	}
	
	public static void debug(String what) {
		log(Level.DEBUG,what);
	}

	public static void warning(String what) {
		log(Level.WARN,what);
	}

	public static void log(Level level, String what) {
		if(logger == null) {
			logger = Logger.getLogger("org.rulez.magwas.worldmodel");
		}
		logger.log(level, what);
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

	public static Document newDocument(ByteArrayInputStream bis) throws SAXException, IOException {
		dBuilder = getDocumentBuilder();
		return dBuilder.parse(bis);
	}

	public static String dom2String(Document doc) throws TransformerException {
		//FIXME is input and output encoding okay
		DOMSource domSource = new DOMSource(doc);
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		StringWriter out = new StringWriter();
		transformer.transform(domSource, new StreamResult(out));
		return out.toString();
	}
	
	public static String xml2String(BaseObject obj) {
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
			return "<exception>Transformation failed</exception>";
		}
	}
}

