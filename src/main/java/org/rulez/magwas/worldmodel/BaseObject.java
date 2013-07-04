package org.rulez.magwas.worldmodel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

@Entity
@Table(name = "object")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BaseObject implements Serializable {
    
    private static final long         serialVersionUID = 1L;
    
    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer                   physid;
    
    @ManyToOne
    @JoinColumn
    private Value                     theid;
    
    @ManyToOne
    @JoinColumn
    private Value                     version;
    
    @ManyToOne
    @JoinColumn
    private BaseObject                type;
    
    @ManyToOne
    @JoinColumn
    private BaseObject                source;
    
    @ManyToOne
    @JoinColumn
    private BaseObject                dest;
    
    @ManyToOne
    @JoinColumn
    private Value                     value;
    
    @Transient
    private Map<String, Serializable> computedFields   = new HashMap<String, Serializable>();
    
    public BaseObject() {
        super();
    }
    
    public BaseObject(Element node, Session session) throws InputParseException {
        if (node == null) {
            throw new InputParseException("got a null");
        }
        String cidstring = node.getAttribute("id");
        if (cidstring.equals("")) {
            throw new InputParseException("object should have an ID");
        }
        BaseObject obj = getBaseObjectByCompositeId(cidstring, session);
        if (obj != null) {
            throw new InputParseException(
                    "there is already an object with this id: " + cidstring);
        }
        List<String> r = parseCompositeId(cidstring);
        this.theid = Value.getValueByValue(r.get(0), session);
        String versionstring = r.get(1);
        if ((versionstring != null) && !versionstring.equals("")) {
            this.version = Value.getValueByValue(versionstring, session);
        }
        BaseObject typeobj = objForField(node, session, cidstring, "type");
        if (null == typeobj) {
            throw new InputParseException("object without type " + cidstring);
        } else {
            type = typeobj;
        }
        source = objForField(node, session, cidstring, "source");
        dest = objForField(node, session, cidstring, "dest");
        String valuestring = node.getAttribute("value");
        if (!valuestring.equals("")) {
            Value tobj = Value.getValueByValue(valuestring, session);
            this.value = tobj;
        }
        session.save(this);
    }
    
    private BaseObject objForField(Element node, Session session,
            String cidstring, String fieldname) throws InputParseException {
        String fieldvalue = node.getAttribute(fieldname);
        if (!fieldvalue.equals("")) {
            if (fieldvalue.equals(cidstring)) {
                return this;
            } else {
                BaseObject tobj = getBaseObjectByCompositeId(fieldvalue,
                        session);
                if (tobj == null) {
                    throw new InputParseException("object for " + fieldname
                            + " does not exists. id=" + fieldvalue);
                }
                return tobj;
            }
        } else {
            return null;
        }
    }
    
    private static List<String> parseCompositeId(String cidstring)
            throws InputParseException {
        String[] parts = cidstring.split(":");
        ArrayList<String> r = new ArrayList<String>();
        if ((parts.length < 1) || (parts.length > 2)) {
            throw new InputParseException("object should have a valid ID");
        }
        r.add(parts[0]);
        String versionstring = null;
        if (parts.length == 2) {
            versionstring = parts[1];
        }
        r.add(versionstring);
        return r;
        
    }
    
    public static BaseObject getBaseObjectByCompositeId(String cidstring,
            Session session) throws InputParseException {
        List<String> r = parseCompositeId(cidstring);
        
        Value idvalue = Value.getValueByValue(r.get(0), session);
        Value versionvalue;
        Query query;
        if (r.get(1) == null) {
            versionvalue = null;
            query = session
                    .createQuery("from BaseObject where theid = :id and version is null");
        } else {
            query = session
                    .createQuery("from BaseObject where theid = :id and version = :version");
            versionvalue = Value.getValueByValue(r.get(1), session);
            query.setParameter("version", versionvalue);
        }
        query.setParameter("id", idvalue);
        List<?> l = query.list();
        BaseObject obj;
        if (l.isEmpty()) {
            obj = null;
        } else {
            obj = (BaseObject) query.list().get(0);
        }
        return obj;
    }
    
    public static List<BaseObject> createFromString(String docstring,
            Session session) throws SAXException, IOException,
            InputParseException, ParserConfigurationException {
        Document doc = Util.newDocument(docstring);
        return createFromXml(doc, session, null, null);
    }
    
    public static List<BaseObject> createFromXml(Document doc, Session session,
            Document outdoc, Element rootnode) throws InputParseException {
        NodeList objs = doc.getElementsByTagName("BaseObject");
        List<BaseObject> created = new ArrayList<BaseObject>();
        for (int i = 0; i < objs.getLength(); i++) {
            BaseObject obj = new BaseObject((Element) objs.item(i), session);
            session.save(obj);
            created.add(obj);
            if (outdoc != null) {
                Element xml;
                xml = obj.toXML(outdoc);
                rootnode.appendChild(xml);
            }
        }
        return created;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other.getClass() != this.getClass()) {
            return false;
        }
        return this.getCompositeId().equals(
                ((BaseObject) other).getCompositeId());
    }
    
    @Override
    public int hashCode() {
        return this.getCompositeId().hashCode() + 1;
    }
    
    /**
     * @return the id
     */
    public Value getId() {
        if (this.theid == null) {
            throw new UnsupportedOperationException("Object without ID");
        }
        return theid;
    }
    
    public void setId(Value theid) {
        this.theid = theid;
    }
    
    /**
     * @return the version
     */
    public Value getVersion() {
        return version;
    }
    
    /**
     * @return the type
     */
    public BaseObject getType() {
        return type;
    }
    
    /**
     * @param type
     *            the type to set
     */
    public void setType(BaseObject type) {
        this.type = type;
    }
    
    /**
     * @return the source
     */
    public BaseObject getSource() {
        return source;
    }
    
    /**
     * @return the dest
     */
    public BaseObject getDest() {
        return dest;
    }
    
    /**
     * @return the value
     */
    public Value getValue() {
        return value;
    }
    
    public String getCompositeId() {
        String idstring = this.getId().getValue();
        String versionstring;
        if (this.version == null) {
            versionstring = "";
        } else {
            versionstring = ":" + this.getVersion().getValue();
        }
        return idstring + versionstring;
    }
    
    private static void setAttrIf(Element e, String name, BaseObject value) {
        if (value != null) {
            e.setAttribute(name, value.getCompositeId());
        }
    }
    
    private String anyObjectToString(Object value) {
        String entrystring;
        if (value == null) {
            throw new UnsupportedOperationException("null entry value");
        }
        if (value instanceof String) {
            entrystring = (String) value;
        } else if (value instanceof Value) {
            entrystring = ((Value) value).getValue();
        } else if (value instanceof BaseObject) {
            entrystring = ((BaseObject) value).getCompositeId();
        } else {
            throw new UnsupportedOperationException(
                    "Unknown type of entry value:"
                            + value.getClass().getCanonicalName());
        }
        return entrystring;
    }
    
    @SuppressWarnings("unchecked")
    public Element toXML(Document doc) {
        Element e = doc.createElement("BaseObject");
        e.setAttribute("id", this.getCompositeId());
        setAttrIf(e, "type", type);
        setAttrIf(e, "source", source);
        setAttrIf(e, "dest", dest);
        if (value != null) {
            e.setAttribute("value", value.getValue());
        }
        for (Entry<String, Serializable> computedField : computedFields
                .entrySet()) {
            if (computedField.getValue() instanceof Map) {
                // expected Map<Value,Value>
                for (Entry<Value, Object> entry : ((Map<Value, Object>) computedField
                        .getValue()).entrySet()) {
                    e.setAttribute(entry.getKey().getValue(),
                            anyObjectToString(entry.getValue()));
                }
            } else if (computedField.getValue() instanceof List) {
                // expected List<String> or List<BaseObject>
                String key = computedField.getKey();
                Element listhead = doc.createElement(key + "s");
                for (Object val : ((List<Object>) computedField.getValue())) {
                    Element item = doc.createElement(key);
                    
                    String itemString = anyObjectToString(val);
                    Text xmlValue = doc.createTextNode(itemString);
                    item.appendChild(xmlValue);
                    listhead.appendChild(item);
                }
                e.appendChild(listhead);
            } else {
                e.setAttribute(computedField.getKey(),
                        anyObjectToString(computedField.getValue()));
            }
        }
        return e;
    }
    
    public Serializable getComputedField(String fieldname) {
        return computedFields.get(fieldname);
    }
    
    public void setComputedField(String key, Serializable value) {
        /*
         * The computed fields are computed by the various plugins from data
         * available in the repository. The main points to have them is:
         * 
         * - make it unnecessary for the client to do complex searches to figure
         * out a feature of an object (like hierarchy children)
         * 
         * - make it unnecessary to reimplement algorithms in the client which
         * have alredy implemented server side
         * 
         * The computed fields can have a limited set of possible types, and
         * given to the client. See toXml for details.
         * 
         * The following goals should be considered:
         * 
         * - the computed fields should be up to date if exist. If a change
         * induces changes in other objects, than they should also be updated,
         * iff they have the field in place.
         * 
         * - the computation should be lazy, to not bring in every object in
         * database to memory
         * 
         * - model belongs to server, view belongs to client.
         * 
         * - a complex model computation which brings in large chunks of objects
         * would do so on the server even if would be performed in the client.
         * The answer here is a plugin.
         * 
         * - avoid code duplication between server and client
         */
        this.computedFields.put(key, value);
    }
    
    public Document toDocument() throws ParserConfigurationException {
        Document doc = Util.newDocument();
        Element xmlobj = toXML(doc);
        doc.appendChild(xmlobj);
        return doc;
    }
    
    public String toXmlString() throws ParserConfigurationException,
            TransformerException {
        Document doc = toDocument();
        return Util.dom2String(doc);
    }
    
    public static BaseObject fromString(String str, Session session)
            throws InputParseException, SAXException, IOException,
            ParserConfigurationException {
        String objstring = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml>"
                + str + "</xml>";
        
        byte[] arr = objstring.getBytes("UTF-8");
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        Document doc = Util.newDocument(bis);
        
        BaseObject obj;
        obj = new BaseObject((Element) doc.getElementsByTagName("BaseObject")
                .item(0), session);
        return obj;
    }
    
}
