package org.rulez.magwas.test;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rulez.magwas.worldmodel.SearchServlet;
import org.rulez.magwas.worldmodel.WorldModelServlet;

import junit.framework.TestCase;

public class SearchServletTest extends TestCase{
	@Before
	protected void setUp() throws Exception {
		WorldModelServlet servlet = new WorldModelServlet();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
	    FileInputStream inputStream = new FileInputStream("src/test/resources/searchtest.xml");//FIXME fill it
        String objstring = IOUtils.toString(inputStream);
		request.setInputString(objstring);
        inputStream.close();
		servlet.doPost(request,response);
		super.setUp();
	}
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Test
	public void testDoGet1() throws ServletException, IOException {
		String retstring = "<objects><BaseObject id=\"idnum\">érték</BaseObject></objects>";
		SearchServlet servlet = new SearchServlet();
		MyHttpServletRequest request = new MyHttpServletRequest();
		MyHttpServletResponse response = new MyHttpServletResponse();
		request.setParameter("key", "src");
		request.setParameter("value", "gettest1");
		request.setParameter("offset", "2");

		servlet.doGet(request,response);
		assertEquals(retstring,TestUtil.NormalizeXmlString(response.getOutput()));
	}
}
