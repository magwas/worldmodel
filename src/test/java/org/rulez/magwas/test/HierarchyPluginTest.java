package org.rulez.magwas.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.rulez.magwas.worldmodel.InputParseException;
import org.rulez.magwas.worldmodel.Util;
import org.rulez.magwas.worldmodel.WorldModelServlet;
import org.xml.sax.SAXException;

public class HierarchyPluginTest {
    
    private static WorldModelServlet servlet;
    
    @Before
    public void before() throws Exception {
        Session session = Util.getSession();
        Transaction tx = session.beginTransaction();
        session.createQuery("delete from BaseObject").executeUpdate();
        tx.commit();
        session.close();
        
        servlet = new WorldModelServlet();
        ServletConfig config = new MockServletConfig();
        servlet.init(config);
    }
    
    @Test
    public void testDerivedContains() throws ServletException, IOException,
            XPathExpressionException, SAXException,
            ParserConfigurationException, InputParseException,
            XPathFactoryConfigurationException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + "<BaseObject id=\"follower\" type=\"contains\"/>"
                + "<BaseObject id=\"followerplace\" type=\"contains\" source=\"hierarchyroot\" dest=\"follower\"/>"
                + "<BaseObject id=\"foo\" type=\"thing\"/>"
                + "<BaseObject id=\"foo follows thing\" type=\"follower\" source=\"thing\" dest=\"foo\"/>"
                + "</xml>";
        
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        Session session = Util.getSession();
        session.close();
        String retstring = response.getOutput();
        TestUtil.assertExpressionOnXmlString("count(//exception)=0", retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='foo']/@parent = 'thing'", retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='follower']/@parent = 'hierarchyroot'",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='follower']/@type = 'contains'", retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='followerplace']/@type = 'contains'",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='followerplace']/@source = 'hierarchyroot'",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='foo']/@type = 'thing'", retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='foo follows thing']/@type = 'follower'",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='foo follows thing']/@source = 'thing'",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='foo follows thing']/@dest = 'foo'",
                retstring);
        
    }
    
    @Test
    public void testNoRelationSource() throws ServletException, IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + "<BaseObject id=\"foonoSource\" type=\"thing\"/>"
                + "<BaseObject id=\"foorel\" type=\"contains\" dest=\"foonoSource\"/>"
                + "</xml>";
        String retstring = "<exception>relation 'foorel' for 'foonoSource' does not have source</exception>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        assertEquals(retstring,
                TestUtil.NormalizeXmlString(response.getOutput()));
    }
    
    @Test
    public void testRelationLoop() throws ServletException, IOException,
            XPathExpressionException, XPathFactoryConfigurationException,
            SAXException, ParserConfigurationException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + "<BaseObject id=\"foo1\" type=\"thing\"/>"
                + "<BaseObject id=\"foorel\" type=\"foorel\" source=\"hierarchyroot\" dest=\"foo1\"/>"
                + "</xml>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        String retstring = response.getOutput();
        TestUtil.assertExpressionOnXmlString("count(//exception)=1", retstring);
        TestUtil.assertExpressionOnXmlString(
                "(//exception = 'relation loop at foorel') or (//exception = 'could not execute statement')",
                retstring);
        
    }
    
    @Test
    public void testTwoParents() throws ServletException, IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + "<BaseObject id=\"foo2\" type=\"thing\"/>"
                + "<BaseObject id=\"foo2rel1\" type=\"contains\" source=\"hierarchyroot\" dest=\"foo2\"/>"
                + "<BaseObject id=\"foo2rel2\" type=\"contains\" source=\"ontology\" dest=\"foo2\"/>"
                + "</xml>";
        String retstring = "<exception>object 'foo2' have at least two parents: 'ontology' and 'hierarchyroot'</exception>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        assertEquals(retstring,
                TestUtil.NormalizeXmlString(response.getOutput()));
    }
    
    @Test
    public void testMultipleRelationUse() throws ServletException, IOException,
            XPathExpressionException, SAXException,
            ParserConfigurationException, XPathFactoryConfigurationException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + "<BaseObject id=\"theroot\" type=\"thing\"/>"
                + "<BaseObject id=\"theroot_rel\" type=\"contains\" source=\"hierarchyroot\" dest=\"theroot\"/>"
                + "<BaseObject id=\"foo3\" type=\"thing\"/>"
                + "<BaseObject id=\"foo4\" type=\"thing\"/>"
                + "<BaseObject id=\"containstoo\" type=\"contains\"/>"
                + "<BaseObject id=\"containstoo2\" type=\"containstoo\"/>"
                + "<BaseObject id=\"foo3rel1\" type=\"containstoo2\" source=\"theroot\" dest=\"foo3\"/>"
                + "<BaseObject id=\"foo2rel4\" type=\"contains\" source=\"theroot\" dest=\"foo4\"/>"
                + "<BaseObject id=\"containstoorel\" type=\"containstoo2\" source=\"theroot\" dest=\"containstoo\"/>"
                + "<BaseObject id=\"containstoo2rel\" type=\"containstoo2\" source=\"theroot\" dest=\"containstoo2\"/>"
                + "</xml>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        String retstring = response.getOutput();
        TestUtil.assertExpressionOnXmlString("count(//exception)=0", retstring);
        TestUtil.assertExpressionOnXmlString(
                "count(//BaseObject[@id='theroot']/kids) = 1", retstring);
        TestUtil.assertExpressionOnXmlString(
                "string-join(//BaseObject[@id='theroot']/kids/kid/text(),',') ="
                        + "'foo3rel1,foo3,foo2rel4,foo4,containstoorel,containstoo,containstoo2rel,containstoo2'",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='theroot']/@parent='hierarchyroot'",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='theroot_rel']/@parent='hierarchyroot'",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='foo3']/@parent='theroot'", retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='foo4']/@parent='theroot'", retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='containstoo']/@parent='theroot'", retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='containstoo2']/@parent='theroot'", retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='containstoorel']/@parent='theroot'",
                retstring);
        TestUtil.assertExpressionOnXmlString(
                "//BaseObject[@id='containstoo2rel']/@parent='theroot'",
                retstring);
        
    }
    
    @Test
    public void testDerivedNoContainRelationUse() throws ServletException,
            IOException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + "<BaseObject id=\"foo5\" type=\"thing\"/>"
                + "<BaseObject id=\"containstoo3\" type=\"relation\"/>"
                + "<BaseObject id=\"containstoo4\" type=\"containstoo3\"/>"
                + "<BaseObject id=\"foo5rel1\" type=\"containstoo4\" source=\"hierarchyroot\" dest=\"foo5\"/>"
                + "<BaseObject id=\"containstoo3rel\" type=\"contains\" source=\"hierarchyroot\" dest=\"containstoo3\"/>"
                + "<BaseObject id=\"containstoo4rel\" type=\"contains\" source=\"ontology\" dest=\"containstoo4\"/>"
                + "</xml>";
        String retstring = "<exception>orphan node foo5</exception>";
        MyHttpServletRequest request = new MyHttpServletRequest();
        MyHttpServletResponse response = new MyHttpServletResponse();
        request.setInputString(objstring);
        servlet.doPost(request, response);
        assertEquals(retstring,
                TestUtil.NormalizeXmlString(response.getOutput()));
    }
    
}
