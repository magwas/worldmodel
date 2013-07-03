package org.rulez.magwas.worldmodel;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.xml.sax.SAXException;

/*
 * Hierarchy.
 * All entities should be reachable within a tree, where the root is the "hierarchyroot" object.
 * The parent of an object is:
 *  - the object referenced in its src, otherwise
 *  - the object referenced in the src of a "contains" relation, or a relation derived from it
 *  
 *  - there should not be two relations of type or derived from "contains" where dest is the same.
 */
public class HierarchyPlugin implements IWorldModelPlugin {
    
    private static final long        serialVersionUID  = 1L;
    static final String              INITOBJECTS       = "<objs>"
                                                               + "<BaseObject id=\"relation\" type=\"thing\" source=\"thing\" dest=\"thing\"/>"
                                                               + "<BaseObject id=\"contains\" type=\"relation\"/>"
                                                               + "<BaseObject id=\"folder\" type=\"thing\"/>"
                                                               + "<BaseObject id=\"hierarchyroot\" type=\"folder\"/>"
                                                               + "<BaseObject id=\"ontology\" type=\"folder\"/>"
                                                               + "<BaseObject id=\"basic ontology\" type=\"folder\"/>"
                                                               + "<BaseObject dest=\"ontology\" id=\"c1\" source=\"hierarchyroot\" type=\"contains\"/>"
                                                               + "<BaseObject dest=\"basic ontology\" id=\"c2\" source=\"ontology\" type=\"contains\"/>"
                                                               + "<BaseObject dest=\"thing\" id=\"c3\" source=\"basic ontology\" type=\"contains\"/>"
                                                               + "<BaseObject dest=\"relation\" id=\"c4\" source=\"basic ontology\" type=\"contains\"/>"
                                                               + "<BaseObject id=\"hierarchy\" type=\"folder\"/>"
                                                               + "<BaseObject dest=\"hierarchy\" id=\"c5\" source=\"ontology\" type=\"contains\"/>"
                                                               + "<BaseObject dest=\"contains\" id=\"c6\" source=\"hierarchy\" type=\"contains\"/>"
                                                               + "<BaseObject dest=\"folder\" id=\"c7\" source=\"hierarchy\" type=\"contains\"/>"
                                                               + "</objs>";
    private Set<BaseObject>          checked           = new HashSet<BaseObject>();
    private BaseObject               hierarchyroot;
    // FIXME: it should be cleared from items added in a failed transaction
    private Map<BaseObject, Boolean> iscontainrelation = new HashMap<BaseObject, Boolean>();
    private BaseObject               thing;
    
    public void init(Session session) throws InputParseException, SAXException,
            IOException, ParserConfigurationException {
        
        hierarchyroot = BaseObject.getBaseObjectByCompositeId("hierarchyroot",
                session);
        if (null == hierarchyroot) {
            BaseObject.createFromString(INITOBJECTS, session);
            hierarchyroot = BaseObject.getBaseObjectByCompositeId(
                    "hierarchyroot", session);
        }
        checked.add(hierarchyroot);
        BaseObject contains = BaseObject.getBaseObjectByCompositeId("contains",
                session);
        iscontainrelation.put(contains, true);
        thing = BaseObject.getBaseObjectByCompositeId("thing", session);
        
    }
    
    public void finalizeObject(Session session, BaseObject obj)
            throws HierarchyInconsistencyException {
        Set<BaseObject> lineage = new HashSet<BaseObject>();
        checkConsistencyOne(session, obj, lineage);
    }
    
    private Boolean isContainRelation(BaseObject relation)
            throws HierarchyInconsistencyException {
        if (iscontainrelation.containsKey(relation)) {
            // FIXME no unit test coverage
            return iscontainrelation.get(relation);
        }
        Set<BaseObject> relationLineage = new HashSet<BaseObject>();
        return isContainRelation(relation, relationLineage);
    }
    
    private Boolean isContainRelation(BaseObject relation,
            Set<BaseObject> relationLineage)
            throws HierarchyInconsistencyException {
        if (relationLineage.contains(relation)) {
            // loop
            throw new HierarchyInconsistencyException("relation loop at "
                    + relation.getCompositeId());
        }
        BaseObject type = relation.getType();
        if (type == null || type.equals(thing)) {
            for (BaseObject r : relationLineage) {
                iscontainrelation.put(r, false);
            }
            return false;
        }
        if (iscontainrelation.containsKey(type)) {
            if (iscontainrelation.get(type)) {
                for (BaseObject r : relationLineage) {
                    iscontainrelation.put(r, true);
                }
                return true;
            }
            for (BaseObject r : relationLineage) {
                iscontainrelation.put(r, false);
            }
            return false;
        }
        relationLineage.add(relation);
        return isContainRelation(type, relationLineage);
    }
    
    private void checkConsistencyOne(Session session, BaseObject obj,
            Set<BaseObject> lineage) throws HierarchyInconsistencyException {
        if (lineage.contains(obj)) {
            // loop
            throw new HierarchyInconsistencyException("object loop at "
                    + obj.getCompositeId());
        }
        if (checked.contains(obj)) {
            for (BaseObject o : lineage) {
                checked.add(o);
            }
            return;
        }
        BaseObject source = obj.getSource();
        if (source != null) {
            lineage.add(obj);
            checkConsistencyOne(session, source, lineage);
        } else {
            Query query = session
                    .createQuery("from BaseObject where dest = :dest");
            query.setParameter("dest", obj);
            @SuppressWarnings("unchecked")
            List<BaseObject> l = query.list();
            BaseObject container = null;
            int containscount = 0;
            for (BaseObject r : l) {
                if (isContainRelation(r)) {
                    containscount += 1;
                    if (containscount > 1) {
                        throw new HierarchyInconsistencyException("object '"
                                + obj.getCompositeId()
                                + "' have at least two parents: '"
                                + r.getCompositeId() + "' and '"
                                + container.getCompositeId() + "'");
                    }
                    container = r;
                }
            }
            if (containscount == 0) {
                throw new HierarchyInconsistencyException("orphan node "
                        + obj.getCompositeId());
            }
            lineage.add(obj);
            checkConsistencyOne(session, container, lineage);
        }
    }
    
    public void checkConsistencyAll(Session session)
            throws InputParseException, HierarchyInconsistencyException {
        checked.clear();
        checked.add(hierarchyroot);
        Query query = session.createQuery("from BaseObject");
        @SuppressWarnings("unchecked")
        List<BaseObject> l = query.list();
        for (BaseObject obj : l) {
            finalizeObject(session, obj);
        }
    }
}
