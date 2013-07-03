package org.rulez.magwas.worldmodel;

import java.util.ArrayList;

import org.hibernate.Query;
import org.hibernate.Session;

public class BasicPlugin implements IWorldModelPlugin {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public void init(Session session) throws Exception {
        
        BaseObject thing;
        try {
            thing = BaseObject.getBaseObjectByCompositeId("thing", session);
        } catch (InputParseException e) {
            Util.die(e);
            // just to make the compiler happy
            return;
        }
        if (thing == null) {
            Util.logInfo("initializing thing");
            thing = new BaseObject();
            thing.setId(Value.getValueByValue("thing", session));
            thing.setType(thing);
            session.save(thing);
        }
        
    }
    
    private void addrev(BaseObject obj, String fieldname, Session session) {
        ArrayList<BaseObject> revsources = new ArrayList<BaseObject>();
        Query query = session.createQuery("from object where source = :"
                + fieldname);
        query.setParameter(fieldname, obj);
        for (Object rsc : query.list()) {
            revsources.add((BaseObject) rsc);
        }
        obj.setComputedField("rev" + fieldname, revsources);
    }
    
    @Override
    public void finalizeObject(Session session, BaseObject obj)
            throws Exception {
        // adds reverse for source, dest and type
        addrev(obj, "source", session);
        addrev(obj, "dest", session);
        addrev(obj, "type", session);
    }
    
    @Override
    public void checkConsistencyAll(Session session) throws Exception {
        // FIXME: check for loops/orphans in the type tree
    }
    
}
