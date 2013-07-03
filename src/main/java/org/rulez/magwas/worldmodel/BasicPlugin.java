package org.rulez.magwas.worldmodel;

import java.util.ArrayList;

import org.hibernate.Query;
import org.hibernate.Session;

public class BasicPlugin implements IWorldModelPlugin {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    public void init(Session session) {
        BaseObject thing;
        try {
            thing = BaseObject.getBaseObjectByCompositeId("thing", session);
        } catch (InputParseException e) {
            WorldModelServlet.die(e);
            // just to make the compiler happy
            return;
        }
        if (thing == null) {
            thing = new BaseObject();
            thing.setId(Value.getValueByValue("thing", session));
            thing.setType(thing);
            session.save(thing);
        }
        
    }
    
    private void addrev(BaseObject obj, String fieldname, Session session) {
        ArrayList<BaseObject> revsources = new ArrayList<BaseObject>();
        Query query = session.createQuery("from BaseObject where " + fieldname
                + " = :" + fieldname);
        query.setParameter(fieldname, obj);
        String revFieldName = "rev" + fieldname;
        for (Object rsc : query.list()) {
            BaseObject referenced = (BaseObject) rsc;
            revsources.add(referenced);
        }
        obj.setComputedField(revFieldName, revsources);
    }
    
    @Override
    public void finalizeObject(Session session, BaseObject obj) {
        if (obj.getComputedField("revtype") != null) {
            return;
        }
        // adds reverse for source, dest and type
        addrev(obj, "source", session);
        addrev(obj, "dest", session);
        addrev(obj, "type", session);
        // checks forward for them
        // nice repetitive code. you gotta love java because the rhythm
        if (obj.getType() != null) {
            addRefToForeign(obj, "revtype");
        }
        if (obj.getSource() != null) {
            addRefToForeign(obj, "revsource");
        }
        if (obj.getDest() != null) {
            addRefToForeign(obj, "revdest");
        }
    }
    
    private void addRefToForeign(BaseObject obj, String fieldname) {
        @SuppressWarnings("unchecked")
        ArrayList<BaseObject> foreignCF = (ArrayList<BaseObject>) obj.getType()
                .getComputedField(fieldname);
        if (foreignCF != null && !foreignCF.contains(obj)) {
            foreignCF.add(obj);
        }
    }
    
    @Override
    public void checkConsistencyAll(Session session) {
        // FIXME: check for loops/orphans in the type tree
    }
    
    @Override
    public void checkConsistency(Session session, BaseObject obj) {
        // do nothing
    }
    
    @Override
    public String getPluginName() {
        return this.getClass().getCanonicalName();
    }
}
