package org.rulez.magwas.worldmodel;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SearchServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Query createQuery(HttpServletRequest request, Session session) throws InputParseException {

		Map<String,Object> queryparms = new HashMap<String,Object>();
		List<String> whereclause = new ArrayList<String>();
		String typestr = request.getParameter("type");
		if(typestr != null && !typestr.equals("")) {
			BaseObject bo = BaseObject.getBaseObjectByCompositeId(typestr, session);
			queryparms.put("type", bo);
			whereclause.add("type = :type");
		}
		
		String sourcestr = request.getParameter("source");
		if(sourcestr != null && !sourcestr.equals("")) {
			BaseObject bo = BaseObject.getBaseObjectByCompositeId(sourcestr, session);
			queryparms.put("source", bo);
			whereclause.add("source = :source");
		}

		String deststr = request.getParameter("dest");
		if(deststr != null && !deststr.equals("")) {
			BaseObject bo = BaseObject.getBaseObjectByCompositeId(deststr, session);
			queryparms.put("dest", bo);
			whereclause.add("dest = :dest");
		}

		String valuestr = request.getParameter("value");
		if(valuestr != null && !valuestr.equals("")) {
			Value v = Value.getValueByValue(valuestr, session);
			queryparms.put("value", v);
			whereclause.add("value = :value");
		}

		String theidstr = request.getParameter("theid");
		if(theidstr != null && !theidstr.equals("")) {
			Value v = Value.getValueByValue(theidstr, session);
			queryparms.put("theid", v);
			whereclause.add("theid = :theid");
		}

		String versionstr = request.getParameter("version");
		if(versionstr != null && !versionstr.equals("")) {
			Value v = Value.getValueByValue(versionstr, session);
			queryparms.put("version", v);
			whereclause.add("version = :version");
		}

		String offsetstr = request.getParameter("offset");
		int offset = 0;
		if(offsetstr != null && !offsetstr.equals("")) {
			offset = Integer.parseInt(offsetstr);
		}

		String querystring = "from BaseObject";
		String wherestring = StringUtils.join(whereclause, " and ");
		if(!wherestring.equals("")) {
			querystring += " where " + wherestring;
		}
		querystring += " order by physid";
		Util.debug(querystring);
		Query query = session.createQuery(querystring);
		
		for(String key : queryparms.keySet()) {
			query.setParameter(key,queryparms.get(key));			
		}
		query.setMaxResults(26);//yes, it is one more. We signal continuation this way.
		Util.debug("offset="+offset);
		query.setFirstResult(offset);

		return query;
		
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/* 
		 * expects zero or more of the following parameters:
		 *  - type: a composite id as the type of the objects we are looking for
		 *  - source: a composite id as the source -+-
		 *  - dest: a composite id as the dest -+-
		 *  - value: a Value as the value -+-
		 *  - theid: a Value as the theid field -+-
		 *  - version: a Value as the version -+-
		 *  - offset: where to start returning the values. as in select ... offset
		 * returns an xml containing BaseObjects corresponding to the query, ordered by physid. Returns at most 25 objects.
		 *   if there are more objects to retrieve, a <continues/> tag is added.
		 */
		
		response.setCharacterEncoding("UTF-8");
		response.addHeader("Content-Type", "text/xml;charset=utf-8");
		PrintWriter out = response.getWriter();

		Session session = Util.getSession();
		Transaction tx = session.beginTransaction();
		
		try {
			
			Query query = createQuery(request, session);

			@SuppressWarnings("unchecked")
			List<BaseObject> l  = query.list();
			Util.debug("number of items: "+l.size());
			Document doc = Util.newDocument();
			Element root = doc.createElement("objects");
			doc.appendChild(root);
			for (BaseObject o :l.subList(0, Math.min(l.size(),25))) {
				Element bo = o.toXML(doc);
				root.appendChild(bo);
			}
			if(l.size() > 25) {
				Element continues = doc.createElement("continues");
				root.appendChild(continues);
			}
			out.print(Util.dom2String(doc));
		} catch (Exception e) {
			out.println("<exception>"+e.getMessage()+"</exception>");
			Util.logException(e);
		} 

    	tx.commit();
    	session.close();		
		out.flush();
        out.close();
	}

}
