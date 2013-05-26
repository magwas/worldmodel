/**
 * 
 */

package org.rulez.magwas.test;


import java.io.ByteArrayInputStream;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.rulez.magwas.worldmodel.BaseObject;
import org.rulez.magwas.worldmodel.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;

/**
 * @author mag
 *
 */
public class BaseObjectTest extends TestCase {

	private BaseObject obj;
	private String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><BaseObject id=\"idnum0\" value=\"öüŐŰőűÖÜ\"></BaseObject></xml>";
	private String outstring = "<objects><BaseObject id=\"idnum0\" value=\"öüŐŰőűÖÜ\"/></objects>";
	private Session session;
	private Transaction tx;
	/**
	 * @param name
	 */
	public BaseObjectTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		Util.debug(objstring);
		Util.hexdump("objstring",objstring);
		byte[] arr = objstring.getBytes("UTF-8");
		ByteArrayInputStream bis = new ByteArrayInputStream(arr);
		Document doc = Util.newDocument(bis);
		Util.hexdump("xml",Util.dom2String(doc));

		session = Util.getSession();
		tx = session.beginTransaction();
		obj = new BaseObject((Element) doc.getElementsByTagName("BaseObject").item(0), session);
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		tx.commit();
		session.close();
	}

	/**
	 * Test method for {@link org.rulez.magwas.worldmodel.BaseObject#getXml()}.
	 */
	public void testGetXml() {
		String str = Util.baseObject2String(obj);
		Util.hexdump("returned",str);
		assertEquals(outstring,TestUtil.NormalizeXmlString(str));
	}

}
