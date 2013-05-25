package org.rulez.magwas.test;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rulez.magwas.worldmodel.WorldModelServlet;

public class WorldModelServletTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDoPostWithSelfReference() throws ServletException, IOException {
		String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"stuff\" type=\"stuff\"/></xml>";
		String retstring = "<objects><BaseObject id=\"stuff\" type=\"stuff\"/></objects>";
		WorldModelServlet servlet = new WorldModelServlet();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		request.setInputString(objstring);
		servlet.doPost(request,response);
		assertEquals(retstring,TestUtil.NormalizeXmlString(response.getOutput()));
	}
	@Test
	public void testDoPostWithSelfReference2() throws ServletException, IOException {
		String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"stuff1\" type=\"stuff1\" source=\"stuff1\" dest=\"stuff1\"/></xml>";
		String retstring = "<objects><BaseObject dest=\"stuff1\" id=\"stuff1\" source=\"stuff1\" type=\"stuff1\"/></objects>";
		WorldModelServlet servlet = new WorldModelServlet();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		request.setInputString(objstring);
		servlet.doPost(request,response);
		assertEquals(retstring,TestUtil.NormalizeXmlString(response.getOutput()));
	}

	@Test
	public void testDoPostTest1() throws ServletException, IOException {
		String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"idnum\">érték</BaseObject></xml>";
		String retstring = "<objects><BaseObject id=\"idnum\">érték</BaseObject></objects>";
		WorldModelServlet servlet = new WorldModelServlet();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		request.setInputString(objstring);
		servlet.doPost(request,response);
		assertEquals(retstring,TestUtil.NormalizeXmlString(response.getOutput()));
	}

	@Test
	public void testDoPostMoreObjects() throws ServletException, IOException {
		String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"idnum2\">érték</BaseObject><BaseObject id=\"idnum3\">érték</BaseObject></xml>";
		String retstring = "<objects><BaseObject id=\"idnum2\">érték</BaseObject><BaseObject id=\"idnum3\">érték</BaseObject></objects>";
		WorldModelServlet servlet = new WorldModelServlet();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		request.setInputString(objstring);
		servlet.doPost(request,response);
		assertEquals(retstring,TestUtil.NormalizeXmlString(response.getOutput()));
	}
	
	@Test
	public void testDoPostTest2() throws ServletException, IOException {
		String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xml>\n<BaseObject id=\"harom\">na micsoda?</BaseObject></xml>";
		String retstring = "<objects><BaseObject id=\"harom\">na micsoda?</BaseObject></objects>";
		WorldModelServlet servlet = new WorldModelServlet();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		request.setInputString(objstring);
		servlet.doPost(request,response);
		assertEquals(retstring,TestUtil.NormalizeXmlString(response.getOutput()));
	}

	@Test
	public void testDoPostAndGet() throws ServletException, IOException {
		String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xml>\n<BaseObject id=\"hehe\">post And Get</BaseObject></xml>";
		String retstring = "<objects><BaseObject id=\"hehe\">post And Get</BaseObject></objects>";
		WorldModelServlet servlet = new WorldModelServlet();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		request.setInputString(objstring);
		servlet.doPost(request,response);
		assertEquals(retstring,TestUtil.NormalizeXmlString(response.getOutput()));
		MyHttpServletRequest request2 = new MyHttpServletRequest();
		MyHttpServletResponse response2 = new MyHttpServletResponse();
		request2.setParameter("id", "hehe");
		servlet.doGet(request2,response2);
		assertEquals(retstring,TestUtil.NormalizeXmlString(response2.getOutput()));
	}

	@Test
	public void testDoPostTestDoubleWrite() throws ServletException, IOException {
		String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xml>\n<BaseObject id=\"negy\">negyedik</BaseObject></xml>";
		String retstring = "<objects><BaseObject id=\"negy\">negyedik</BaseObject></objects>";
		WorldModelServlet servlet = new WorldModelServlet();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		request.setInputString(objstring);
		servlet.doPost(request,response);
		assertEquals(retstring,TestUtil.NormalizeXmlString(response.getOutput()));
		MyHttpServletRequest request2 = new MyHttpServletRequest();
		MyHttpServletResponse response2 = new MyHttpServletResponse();
		request2.setInputString(objstring);
		servlet.doPost(request2,response2);
		assertEquals("<exception>there is already an object with this id: negy</exception>",response2.getOutput());		
	}

	@Test
	public void testDoGetNull() throws ServletException, IOException {
		WorldModelServlet servlet = new WorldModelServlet();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		servlet.doGet(request,response);
		assertEquals(response.getOutput(),"<null>\n");
	}

}
