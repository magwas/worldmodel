/**
 * 
 */

package org.rulez.magwas.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rulez.magwas.worldmodel.BaseObject;
import org.rulez.magwas.worldmodel.InputParseException;
import org.rulez.magwas.worldmodel.Util;
import org.rulez.magwas.worldmodel.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author mag
 * 
 */
public class BaseObjectTest {
    
    private static Session session;
    
    @BeforeClass
    public static void setUp() throws SAXException, IOException,
            InputParseException, ParserConfigurationException {
        session = Util.getSession();
        BaseObject obj = BaseObject
                .getBaseObjectByCompositeId("thing", session);
        if (obj == null) {
            String objstring = "<BaseObject id=\"thing\" type=\"thing\"/>";
            byte[] arr = objstring.getBytes("UTF-8");
            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            Document doc = Util.newDocument(bis);
            Transaction tx = session.beginTransaction();
            new BaseObject((Element) doc.getElementsByTagName("BaseObject")
                    .item(0), session);
            tx.commit();
            
        }
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        session.close();
    }
    
    @Test
    public void testCreate() throws Exception {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"idnum0\" value=\"öüŐŰőűÖÜ\" type=\"thing\"></BaseObject></xml>";
        String outstring = "<objects><BaseObject id=\"idnum0\" type=\"thing\" value=\"öüŐŰőűÖÜ\"/></objects>";
        
        byte[] arr = objstring.getBytes("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Document doc = Util.newDocument(bis);
        
        Transaction tx = session.beginTransaction();
        BaseObject obj = new BaseObject((Element) doc.getElementsByTagName(
                "BaseObject").item(0), session);
        String str = Util.baseObject2String(obj);
        assertEquals(outstring, TestUtil.NormalizeXmlString(str));
        tx.commit();
    }
    
    @Test(expected = InputParseException.class)
    public void testCreateNull() throws Exception {
        new BaseObject(null, session);
        
    }
    
    @Test(expected = InputParseException.class)
    public void testCreateNoId() throws Exception {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject value=\"öüŐŰőűÖÜ\" type=\"thing\"></BaseObject></xml>";
        
        byte[] arr = objstring.getBytes("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Document doc = Util.newDocument(bis);
        
        new BaseObject(
                (Element) doc.getElementsByTagName("BaseObject").item(0),
                session);
    }
    
    @Test(expected = InputParseException.class)
    public void testCreateNoType() throws Exception {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject value=\"öüŐŰőűÖÜ\" id=\"thought\"></BaseObject></xml>";
        
        byte[] arr = objstring.getBytes("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Document doc = Util.newDocument(bis);
        
        new BaseObject(
                (Element) doc.getElementsByTagName("BaseObject").item(0),
                session);
    }
    
    @Test(expected = InputParseException.class)
    public void testCreateTooManyPartsId() throws Exception {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"foo:bar:baz\" value=\"öüŐŰőűÖÜ\" type=\"thing\"></BaseObject></xml>";
        
        byte[] arr = objstring.getBytes("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Document doc = Util.newDocument(bis);
        
        new BaseObject(
                (Element) doc.getElementsByTagName("BaseObject").item(0),
                session);
    }
    
    @Test
    public void testCreateVersionedId() throws Exception {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"idnum0:foo\" value=\"öüŐŰőűÖÜ\" type=\"thing\"></BaseObject></xml>";
        String outstring = "<objects><BaseObject id=\"idnum0:foo\" type=\"thing\" value=\"öüŐŰőűÖÜ\"/></objects>";
        
        byte[] arr = objstring.getBytes("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Document doc = Util.newDocument(bis);
        
        Transaction tx = session.beginTransaction();
        BaseObject obj = new BaseObject((Element) doc.getElementsByTagName(
                "BaseObject").item(0), session);
        String str = Util.baseObject2String(obj);
        assertEquals(outstring, TestUtil.NormalizeXmlString(str));
        tx.commit();
    }
    
    @Test(expected = InputParseException.class)
    public void testCreateInvalidType() throws Exception {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"fooo\" value=\"öüŐŰőűÖÜ\" type=\"this type does not exist\"></BaseObject></xml>";
        
        byte[] arr = objstring.getBytes("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Document doc = Util.newDocument(bis);
        
        new BaseObject(
                (Element) doc.getElementsByTagName("BaseObject").item(0),
                session);
    }
    
    @Test(expected = InputParseException.class)
    public void testCreateInvalidSource() throws Exception {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"fooo\" value=\"öüŐŰőűÖÜ\" source=\"this source does not exist\" type=\"thing\"></BaseObject></xml>";
        
        byte[] arr = objstring.getBytes("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Document doc = Util.newDocument(bis);
        
        new BaseObject(
                (Element) doc.getElementsByTagName("BaseObject").item(0),
                session);
    }
    
    @Test(expected = InputParseException.class)
    public void testCreateInvalidDest() throws Exception {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"fooo\" value=\"öüŐŰőűÖÜ\" dest=\"this dest does not exist\" type=\"thing\"></BaseObject></xml>";
        
        byte[] arr = objstring.getBytes("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Document doc = Util.newDocument(bis);
        
        new BaseObject(
                (Element) doc.getElementsByTagName("BaseObject").item(0),
                session);
    }
    
    @Test
    public void testTheRest() throws Exception {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + "<BaseObject id=\"one\" type=\"thing\"/>"
                + "<BaseObject id=\"two\" type=\"thing\"/>"
                + "<BaseObject id=\"idnum1:foo\"  type=\"thing\" value=\"öüŐŰőűÖÜ\" source=\"one\" dest=\"two\"></BaseObject>"
                + "</xml>";
        String outstring = "<objects>"
                + "<BaseObject id=\"one\" type=\"thing\"/>"
                + "<BaseObject id=\"two\" type=\"thing\"/>"
                + "<BaseObject dest=\"two\" id=\"idnum1:foo\" source=\"one\" type=\"thing\" value=\"öüŐŰőűÖÜ\"/>"
                + "</objects>";
        
        byte[] arr = objstring.getBytes("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Document doc = Util.newDocument(bis);
        Document outdoc = Util.newDocument();
        Element rootElement = outdoc.createElement("objects");
        outdoc.appendChild(rootElement);
        BaseObject.createFromXml(doc, session, outdoc, rootElement);
        BaseObject obj = BaseObject.getBaseObjectByCompositeId("idnum1:foo",
                session);
        assertEquals(outstring,
                TestUtil.NormalizeXmlString(Util.dom2String(outdoc)));
        assertEquals(false, obj.equals(null));
        assertEquals(false, obj.equals("This is not a pipe"));
        assertEquals("foo", obj.getVersion().getValue());
        assertEquals("two", obj.getDest().getCompositeId());
        assertEquals("one", obj.getSource().getCompositeId());
        assertEquals("öüŐŰőűÖÜ", obj.getValue().getValue());
        assertEquals(null, Value.getValueByValue("", session));
        assertEquals(null, Value.getValueByValue(null, session));
        assertEquals(true, 0 != Value.getValueByValue("thing", session).getId());
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyObjectGetId() throws Exception {
        BaseObject obj = new BaseObject();
        obj.getId();
    }
    
}
