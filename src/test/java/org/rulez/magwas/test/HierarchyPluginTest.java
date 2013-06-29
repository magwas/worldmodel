package org.rulez.magwas.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.rulez.magwas.worldmodel.WorldModelServlet;

public class HierarchyPluginTest {
    
    private static WorldModelServlet servlet;
    
    @BeforeClass
    public static void before() throws Exception {
        servlet = new WorldModelServlet();
        ServletConfig config = new MockServletConfig();
        servlet.init(config);
    }
    
    @Test
    public void testDerivedContains() throws ServletException, IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + "<BaseObject id=\"follower\" type=\"contains\"/>"
                + "<BaseObject id=\"followerplace\" type=\"contains\" source=\"hierarchyroot\" dest=\"follower\"/>"
                + "<BaseObject id=\"foo\" type=\"thing\"/>"
                + "<BaseObject id=\"foo follows thing\" type=\"follower\" source=\"thing\" dest=\"foo\"/>"
                + "</xml>";
        String retstring = "<objects>"
                + "<BaseObject id=\"follower\" type=\"contains\"/>"
                + "<BaseObject dest=\"follower\" id=\"followerplace\" source=\"hierarchyroot\" type=\"contains\"/>"
                + "<BaseObject id=\"foo\" type=\"thing\"/>"
                + "<BaseObject dest=\"foo\" id=\"foo follows thing\" source=\"thing\" type=\"follower\"/>"
                + "</objects>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        assertEquals(retstring,
                TestUtil.NormalizeXmlString(response.getOutput()));
    }
    
    @Test
    public void testRelationLoop() throws ServletException, IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + "<BaseObject id=\"foo1\" type=\"thing\"/>"
                + "<BaseObject id=\"foorel\" type=\"foorel\" source=\"hierarchyroot\" dest=\"foo1\"/>"
                + "</xml>";
        String retstring = "<exception>relation loop at foorel</exception>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        assertEquals(retstring,
                TestUtil.NormalizeXmlString(response.getOutput()));
    }
    
    @Test
    public void testTwoParents() throws ServletException, IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + "<BaseObject id=\"foo2\" type=\"thing\"/>"
                + "<BaseObject id=\"foo2rel1\" type=\"contains\" source=\"hierarchyroot\" dest=\"foo2\"/>"
                + "<BaseObject id=\"foo2rel2\" type=\"contains\" source=\"ontology\" dest=\"foo2\"/>"
                + "</xml>";
        String retstring = "<exception>object foo2 have at least two parents: foo2rel2 and foo2rel1</exception>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        assertEquals(retstring,
                TestUtil.NormalizeXmlString(response.getOutput()));
    }
    
}
