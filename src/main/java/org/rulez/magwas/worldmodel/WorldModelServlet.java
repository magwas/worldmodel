package org.rulez.magwas.worldmodel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WorldModelServlet extends HttpServlet {
    /**
     * serial version just to get rid of warning
     */
    private static final long serialVersionUID = 1L;
    private static final int  NUMENTRIES       = 25;
    PluginManager             plugins          = null;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        String pluginstackConfig = config.getInitParameter("PluginStack");
        Session session = Util.getSession();
        Transaction tx = session.beginTransaction();
        try {
            plugins = new PluginManager(pluginstackConfig);
            plugins.init(session);
            tx.commit();
        } catch (Throwable e) {
            tx.rollback();
            Util.die(e);
        }
        session.close();
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        /*
         * expects a xml containing a BaseObject entity in the same form as get
         * returns it back
         */
        BufferedReader reader = null;
        String str = "";
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Content-Type", "text/xml;charset=utf-8");
        PrintWriter out = response.getWriter();
        Session session = Util.getSession();
        Transaction tx = session.beginTransaction();
        try {
            if (Util.isStopped) {
                throw new IOException("Fatal error occured");
            }
            reader = request.getReader();
            String xmlstring = IOUtils.toString(reader);
            // FIXME schema validation (maybe in upper layers)
            Document doc = Util.newDocument(xmlstring);
            
            NodeList objs = doc.getElementsByTagName("BaseObject");
            Document outdoc = Util.newDocument();
            Element rootnode = outdoc.createElement("objects");
            outdoc.appendChild(rootnode);
            List<BaseObject> newobjs = new ArrayList<BaseObject>();
            for (int i = 0; i < objs.getLength(); i++) {
                BaseObject obj = new BaseObject((Element) objs.item(i), session);
                session.save(obj);
                newobjs.add(obj);
            }
            for (BaseObject candidate : newobjs) {
                plugins.checkConsistency(session, candidate);
                plugins.finalizeObject(session, candidate);
                Element xml;
                xml = candidate.toXML(outdoc);
                rootnode.appendChild(xml);
            }
            
            str = Util.dom2String(outdoc);
            tx.commit();
            
        } catch (Throwable e) {
            tx.rollback();
            str = "<exception>" + e.getMessage() + "</exception>";
            Util.logException(e);
        }
        
        session.close();
        out.write(str);
        out.flush();
        out.close();
        
    }
    
    private Query createQuery(HttpServletRequest request, Session session)
            throws InputParseException {
        // FIXME: some query optimisation/caching is possible here
        
        Map<String, Object> queryparms = new HashMap<String, Object>();
        List<String> whereclause = new ArrayList<String>();
        
        String typestr = request.getParameter("type");
        if (typestr != null && !typestr.equals("")) {
            BaseObject bo = BaseObject.getBaseObjectByCompositeId(typestr,
                    session);
            queryparms.put("type", bo);
            whereclause.add("type = :type");
        }
        
        String sourcestr = request.getParameter("source");
        if (sourcestr != null && !sourcestr.equals("")) {
            BaseObject bo = BaseObject.getBaseObjectByCompositeId(sourcestr,
                    session);
            queryparms.put("source", bo);
            whereclause.add("source = :source");
        }
        
        String deststr = request.getParameter("dest");
        if (deststr != null && !deststr.equals("")) {
            BaseObject bo = BaseObject.getBaseObjectByCompositeId(deststr,
                    session);
            queryparms.put("dest", bo);
            whereclause.add("dest = :dest");
        }
        
        String valuestr = request.getParameter("value");
        if (valuestr != null && !valuestr.equals("")) {
            Value v = Value.getValueByValue(valuestr, session);
            queryparms.put("value", v);
            whereclause.add("value = :value");
        }
        
        String theidstr = request.getParameter("theid");
        if (theidstr != null && !theidstr.equals("")) {
            Value v = Value.getValueByValue(theidstr, session);
            queryparms.put("theid", v);
            whereclause.add("theid = :theid");
        }
        
        String versionstr = request.getParameter("version");
        if (versionstr != null && !versionstr.equals("")) {
            Value v = Value.getValueByValue(versionstr, session);
            queryparms.put("version", v);
            whereclause.add("version = :version");
        }
        
        String offsetstr = request.getParameter("offset");
        int offset = 0;
        if (offsetstr != null && !offsetstr.equals("")) {
            offset = Integer.parseInt(offsetstr);
        }
        
        String querystring = "from BaseObject";
        String wherestring = StringUtils.join(whereclause, " and ");
        if (!wherestring.equals("")) {
            querystring += " where " + wherestring;
        }
        querystring += " order by physid";
        Util.debug(querystring);
        Query query = session.createQuery(querystring);
        for (Entry<String, Object> entry : queryparms.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        // yes, it is one more. We signal continuation this way.
        query.setMaxResults(NUMENTRIES + 1);
        Util.debug("offset=" + offset);
        query.setFirstResult(offset);
        
        return query;
        
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // FIXME should only convert the get to post and submit
        /*
         * expects zero or more of the following parameters: - id: a composite
         * id of the object we are looking for - type: a composite id as the
         * type of the objects we are looking for - source: a composite id as
         * the source -+- - dest: a composite id as the dest -+- - value: a
         * Value as the value -+- - theid: a Value as the theid field -+- -
         * version: a Value as the version -+- - offset: where to start
         * returning the values. as in select ... offset
         * 
         * returns an xml containing one BaseObject entity, with the following
         * attributes: - id - version - type - source - dest - value
         * 
         * all attributes are in form <id>[:<version>]
         * 
         * returns an xml containing BaseObjects corresponding to the query,
         * ordered by physid. Returns at most 25 objects. If there are more
         * objects to retrieve, a <continues/> tag is added.
         */
        
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Content-Type", "text/xml;charset=utf-8");
        PrintWriter out = response.getWriter();
        
        Session session = Util.getSession();
        Transaction tx = session.beginTransaction();
        
        try {
            if (Util.isStopped) {
                throw new IOException("Fatal error occured");
            }
            Document doc = Util.newDocument();
            Element root = doc.createElement("objects");
            List<BaseObject> l;
            String idstr = request.getParameter("id");
            if (idstr != null && !idstr.equals("")) {
                BaseObject bo = BaseObject.getBaseObjectByCompositeId(idstr,
                        session);
                
                l = new ArrayList<BaseObject>();
                l.add(bo);
            } else {
                Query query = createQuery(request, session);
                
                @SuppressWarnings("unchecked")
                // variable introduced just to make the scope of
                // SuppressWarnings small
                List<BaseObject> ql = (List<BaseObject>) query.list();
                l = ql;
                
            }
            Util.debug("number of items: " + l.size());
            doc.appendChild(root);
            for (BaseObject o : l.subList(0, Math.min(l.size(), NUMENTRIES))) {
                Element bo = o.toXML(doc);
                root.appendChild(bo);
            }
            if (l.size() > NUMENTRIES) {
                Element continues = doc.createElement("continues");
                root.appendChild(continues);
            }
            out.print(Util.dom2String(doc));
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            out.println("<exception>" + e.getMessage() + "</exception>");
            Util.logException(e);
        }
        
        session.close();
        out.flush();
        out.close();
    }
    
}
