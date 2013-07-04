/**
 * 
 */

package org.rulez.magwas.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
    
    private static Session    session;
    private static BaseObject thing;
    
    @BeforeClass
    public static void setUp() throws SAXException, IOException,
            InputParseException, ParserConfigurationException {
        System.out.println("setUp");
        session = Util.getSession();
        System.out.println("got session");
        thing = BaseObject.getBaseObjectByCompositeId("thing", session);
        System.out.println("got thing");
        if (thing == null) {
            System.out.println("thing is null");
            String objstring = "<BaseObject id=\"thing\" type=\"thing\"/>";
            byte[] arr = objstring.getBytes("UTF-8");
            ByteArrayInputStream bis = new ByteArrayInputStream(arr);
            Document doc = Util.newDocument(bis);
            Transaction tx = session.beginTransaction();
            thing = new BaseObject((Element) doc.getElementsByTagName(
                    "BaseObject").item(0), session);
            System.out.println("before commit");
            tx.commit();
            System.out.println("after commit");
            
        }
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        System.out.println("tearDown");
        session.close();
    }
    
    @Test
    public void testCreate() throws Exception {
        System.out.println("create");
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"idnum0\" value=\"öüŐŰőűÖÜ\" type=\"thing\"></BaseObject></xml>";
        String outstring = "<BaseObject id=\"idnum0\" type=\"thing\" value=\"öüŐŰőűÖÜ\"/>";
        
        byte[] arr = objstring.getBytes("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Document doc = Util.newDocument(bis);
        
        Transaction tx = session.beginTransaction();
        BaseObject obj = new BaseObject((Element) doc.getElementsByTagName(
                "BaseObject").item(0), session);
        String str = obj.toXmlString();
        assertEquals(outstring, TestUtil.NormalizeXmlString(str));
        tx.commit();
    }
    
    @Test
    public void testComputedFields() throws Exception {
        System.out.println("testComputedFields");
        BaseObject bo = new BaseObject();
        bo.setId(Value.getValueByValue("testComputed", session));
        bo.setType(bo);
        bo.setComputedField("anattribute", "astring");
        bo.setComputedField("anattribute2",
                Value.getValueByValue("avalue", session));
        
        HashMap<Value, Value> testValuedMap = new HashMap<Value, Value>();
        testValuedMap.put(Value.getValueByValue("mappedkey1", session),
                Value.getValueByValue("mappedval1", session));
        testValuedMap.put(Value.getValueByValue("mappedkey2", session),
                Value.getValueByValue("mappedval2", session));
        testValuedMap.put(Value.getValueByValue("mappedkey3", session),
                Value.getValueByValue("mappedval3", session));
        bo.setComputedField("valuedMap", testValuedMap);
        
        bo.setComputedField("objattrib", bo);
        HashMap<Value, String> testStringMap = new HashMap<Value, String>();
        testStringMap.put(Value.getValueByValue("mappedstringkey1", session),
                "mappedstringval1");
        testStringMap.put(Value.getValueByValue("mappedstringkey2", session),
                "mappedstringval2");
        testStringMap.put(Value.getValueByValue("mappedstringkey3", session),
                "mappedstringval3");
        bo.setComputedField("stringMap", testStringMap);
        
        ArrayList<BaseObject> objectList = new ArrayList<BaseObject>();
        objectList.add(bo);
        objectList.add(thing);
        bo.setComputedField("object", objectList);
        
        ArrayList<String> stringList = new ArrayList<String>();
        stringList.add("one");
        stringList.add("two");
        stringList.add("three");
        bo.setComputedField("string", stringList);
        session.save(bo);
        
        String outstring = "<BaseObject anattribute=\"astring\" anattribute2=\"avalue\" "
                + "id=\"testComputed\" "
                + "mappedkey1=\"mappedval1\" mappedkey2=\"mappedval2\" mappedkey3=\"mappedval3\" "
                + "mappedstringkey1=\"mappedstringval1\" mappedstringkey2=\"mappedstringval2\" "
                + "mappedstringkey3=\"mappedstringval3\" "
                + "objattrib=\"testComputed\" type=\"testComputed\">"
                + "<strings><string>one</string><string>two</string><string>three</string></strings>"
                + "<objects><object>testComputed</object><object>thing</object></objects>"
                + "</BaseObject>";
        
        String str = bo.toXmlString();
        assertEquals(outstring, TestUtil.NormalizeXmlString(str));
    }
    
    @Test(expected = InputParseException.class)
    public void testCreateNull() throws Exception {
        System.out.println("testCreateNull");
        new BaseObject(null, session);
        
    }
    
    @Test(expected = InputParseException.class)
    public void testCreateNoId() throws Exception {
        System.out.println("testCreateNoId");
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
        System.out.println("testCreateNoType");
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
        System.out.println("testCreateTooManyPartsId");
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
        System.out.println("testCreateVersionedId");
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"idnum0:foo\" value=\"öüŐŰőűÖÜ\" type=\"thing\"></BaseObject></xml>";
        String outstring = "<BaseObject id=\"idnum0:foo\" type=\"thing\" value=\"öüŐŰőűÖÜ\"/>";
        
        byte[] arr = objstring.getBytes("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Document doc = Util.newDocument(bis);
        
        Transaction tx = session.beginTransaction();
        BaseObject obj = new BaseObject((Element) doc.getElementsByTagName(
                "BaseObject").item(0), session);
        String str = obj.toXmlString();
        assertEquals(outstring, TestUtil.NormalizeXmlString(str));
        tx.commit();
    }
    
    @Test(expected = InputParseException.class)
    public void testCreateInvalidType() throws Exception {
        System.out.println("testCreateInvalidType");
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
        System.out.println("testCreateInvalidSource");
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
        System.out.println("testCreateInvalidDest");
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
        System.out.println("testTheRest");
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
        System.out.println("testEmptyObjectGetId");
        BaseObject obj = new BaseObject();
        obj.getId();
    }
    
}
