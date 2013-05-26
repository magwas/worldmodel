package org.rulez.magwas.test;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rulez.magwas.worldmodel.InputParseException;
import org.rulez.magwas.worldmodel.SearchServlet;
import org.rulez.magwas.worldmodel.Util;
import org.rulez.magwas.worldmodel.WorldModelServlet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import junit.framework.TestCase;

public class SearchServletTest extends TestCase{
	
	private static boolean isUp = false;//FIXME could not get BeforeClass to work
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		if(isUp) return;
		WorldModelServlet servlet = new WorldModelServlet();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
	    FileInputStream inputStream = new FileInputStream("src/test/resources/searchtest.xml");//FIXME fill it
        String objstring = IOUtils.toString(inputStream);
        System.out.println(objstring);
		request.setInputString(objstring);
        inputStream.close();
		servlet.doPost(request,response);
		String output = response.getOutput();
		Document retdoc = Util.newDocument(output);
		if(retdoc.getElementsByTagName("exception").getLength() != 0) {
			throw new InputParseException(output);
		}
		isUp=true;
		
	}
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testDoGetRightOffset() throws Exception {
		SearchServlet servlet = new SearchServlet();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		//request.setParameter("value", "gettest1");
		//request.setParameter("offset", "2");

		servlet.doGet(request,response);
		String output = response.getOutput();

		Document retdoc = Util.newDocument(output);
		assertEquals(0,retdoc.getElementsByTagName("exception").getLength());
		assertEquals(1,retdoc.getElementsByTagName("objects").getLength());
		assertEquals(25,retdoc.getElementsByTagName("BaseObject").getLength());
		assertEquals(1,retdoc.getElementsByTagName("continues").getLength());
		
		NodeList bolist1 = retdoc.getElementsByTagName("BaseObject");

		MyHttpServletRequest request2 = new MyHttpServletRequest();
		MyHttpServletResponse response2 = new MyHttpServletResponse();
		request2.setParameter("offset", "15");

		servlet.doGet(request2,response2);
		String output2 = response2.getOutput();

		Document retdoc2 = Util.newDocument(output2);
		assertEquals(0,retdoc2.getElementsByTagName("exception").getLength());
		assertEquals(1,retdoc2.getElementsByTagName("objects").getLength());
		assertEquals(25,retdoc2.getElementsByTagName("BaseObject").getLength());
		assertEquals(1,retdoc2.getElementsByTagName("continues").getLength());

		NodeList bolist2 = retdoc2.getElementsByTagName("BaseObject");
		Element bo1 = (Element) bolist1.item(8+15);
		Element bo2 = (Element) bolist2.item(8);
		assertEquals(bo1.getAttribute("id"),bo2.getAttribute("id"));
		assertEquals(bo1.getAttribute("source"),bo2.getAttribute("source"));
		assertEquals(bo1.getAttribute("type"),bo2.getAttribute("type"));
		assertEquals(bo1.getAttribute("dest"),bo2.getAttribute("dest"));

	}
	
	@Test
	public void testDoGetFetchAllConsistency() throws Exception {
		SearchServlet servlet = new SearchServlet();
		int offset = 0;
		boolean doit = true;
		List<String> idlist = new ArrayList<String>();
		while(doit) {
			MyHttpServletRequest request = new MyHttpServletRequest();
			MyHttpServletResponse response = new MyHttpServletResponse();
			request.setParameter("offset", Integer.toString(offset));

			servlet.doGet(request,response);
			String output = response.getOutput();

			Document retdoc = Util.newDocument(output);
			assertEquals(0,retdoc.getElementsByTagName("exception").getLength());
			assertEquals(1,retdoc.getElementsByTagName("objects").getLength());
			NodeList bolist = retdoc.getElementsByTagName("BaseObject");
			for(int i=0; i<bolist.getLength();i++) {
				String idstr = ((Element)bolist.item(i)).getAttribute("id");
				assert(!idlist.contains(idstr));
				idlist.add(idstr);
			}
			if(0 == retdoc.getElementsByTagName("continues").getLength()) {
				doit=false;
			} else {
				offset += 25;
			}
		}
	}

	@Test
	public void testDoGetFetchOneType() throws Exception {
		SearchServlet servlet = new SearchServlet();
		List<String> idlist = new ArrayList<String>();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		request.setParameter("type", "contains");

		servlet.doGet(request,response);
		String output = response.getOutput();
		System.out.println("\n\n\n\n===================RESPONSE===========================\n\n\n");
		System.out.println(output);

		Document retdoc = Util.newDocument(output);
		assertEquals(0,retdoc.getElementsByTagName("exception").getLength());
		assertEquals(1,retdoc.getElementsByTagName("objects").getLength());
		NodeList bolist = retdoc.getElementsByTagName("BaseObject");
		for(int i=0; i<bolist.getLength();i++) {
			String idstr = ((Element)bolist.item(i)).getAttribute("id");
			String typestr = ((Element)bolist.item(i)).getAttribute("type");
			assert(!idlist.contains(idstr));
			assertEquals(typestr,"contains");
			idlist.add(idstr);
		}
		assertEquals(19,bolist.getLength());
		assertEquals(0,retdoc.getElementsByTagName("continues").getLength());
	}
	
	@Test
	public void testDoGetFetchOneSource() throws Exception {
		SearchServlet servlet = new SearchServlet();
		List<String> idlist = new ArrayList<String>();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		request.setParameter("source", "thing");

		servlet.doGet(request,response);
		String output = response.getOutput();
		System.out.println("\n\n\n\n===================RESPONSE===========================\n\n\n");
		System.out.println(output);

		Document retdoc = Util.newDocument(output);
		assertEquals(0,retdoc.getElementsByTagName("exception").getLength());
		assertEquals(1,retdoc.getElementsByTagName("objects").getLength());
		NodeList bolist = retdoc.getElementsByTagName("BaseObject");
		for(int i=0; i<bolist.getLength();i++) {
			String idstr = ((Element)bolist.item(i)).getAttribute("id");
			String typestr = ((Element)bolist.item(i)).getAttribute("source");
			assert(!idlist.contains(idstr));
			assertEquals(typestr,"thing");
			idlist.add(idstr);
		}
		assertEquals(2,bolist.getLength());
		assertEquals(0,retdoc.getElementsByTagName("continues").getLength());
	}
	@Test
	public void testDoGetFetchOneDest() throws Exception {
		SearchServlet servlet = new SearchServlet();
		List<String> idlist = new ArrayList<String>();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		request.setParameter("dest", "One");

		servlet.doGet(request,response);
		String output = response.getOutput();
		System.out.println("\n\n\n\n===================RESPONSE===========================\n\n\n");
		System.out.println(output);

		Document retdoc = Util.newDocument(output);
		assertEquals(0,retdoc.getElementsByTagName("exception").getLength());
		assertEquals(1,retdoc.getElementsByTagName("objects").getLength());
		NodeList bolist = retdoc.getElementsByTagName("BaseObject");
		for(int i=0; i<bolist.getLength();i++) {
			String idstr = ((Element)bolist.item(i)).getAttribute("id");
			String deststr = ((Element)bolist.item(i)).getAttribute("dest");
			assert(!idlist.contains(idstr));
			assertEquals(deststr,"One");
			idlist.add(idstr);
		}
		assertEquals(9,bolist.getLength());
		assertEquals(0,retdoc.getElementsByTagName("continues").getLength());
	}
}
