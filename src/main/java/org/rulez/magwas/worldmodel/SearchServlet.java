package org.rulez.magwas.worldmodel;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SearchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Query createQuery(String key, String value, String offset, Session session) throws InputParseException {
		//from BaseObject where <key> <qualifier> limit 25 offset <offset>
		// in case of type, source, dest and existing value equality is for the object with the given id,
		// else it is for a Value with the given value
		String qualifier;
		String offsetpart;
		if(offset == null || offset.equals("")) {
			offsetpart = "";
		} else {
			offsetpart = "offset :offset";
		}
		if((value == null) || (value.equals(""))) {
			qualifier = "is null";
		} else {
			qualifier = " = :value";
		}
		
		Query query = session.createQuery("from BaseObject where "+key+qualifier+" limit 25 "+offsetpart);

		if(!offsetpart.equals("")){
			query.setParameter("offset",Integer.parseInt(offset));
		}
		if((value != null) && (!value.equals(""))) {
			switch(key) {
			case "type":			
			case "source":
			case "dest":
				query.setParameter("value", BaseObject.getBaseObjectByCompositeId(value, session));
				break;
			case "value":
			case "theid":
			case "version":
				query.setParameter("value", Value.getValueByValue(value, session));
				break;
			default:
				throw new InputParseException("unknown search key");
			}		
		}
		return query;
		
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/*
		 * expects the following parameters:
		 *  - searchkey: in {"type", "source", "dest", "value", "theid", "version" }. mandatory
		 *  - value: a string. in case of type, source or dest, it is a composite id.
		 *       in case of theid, version or value it is a value
		 *       if it is empty, we are looking for "is null"
		 *  - offset: where to start returning the values. as in select ... offset
		 * returns an xml containing BaseObjects corresponding to the query. Returns at most 25 objects.
		 *   if there are more objects to retrieve, a <continues/> tag is added.
		 */
		
		String key=request.getParameter("key");
		String value=request.getParameter("value");
		String offset=request.getParameter("offset");
		Session session = Util.getSession();
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Content-Type", "text/xml;charset=utf-8");
		PrintWriter out = response.getWriter();
			
		Transaction tx = session.beginTransaction();
		Query query;
		try {
			query = createQuery(key, value, offset, session);
			@SuppressWarnings("unchecked")
			List<BaseObject> l  = query.list();//FIXME signal if we have more
			Document doc = Util.newDocument();
			Element root = doc.createElement("objects");
			doc.appendChild(root);
			for (BaseObject o :l) {
				Element bo = o.toXML(doc);
				root.appendChild(bo);
			}
			out.print(Util.dom2String(doc));
		} catch (Exception e) {
			out.println("<exception>"+e.getMessage()+"</exception>");
		} 

    	tx.commit();
    	session.close();		
		out.flush();
        out.close();
	}

}
