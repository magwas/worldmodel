package org.rulez.magwas.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rulez.magwas.worldmodel.PluginManager;
import org.rulez.magwas.worldmodel.Util;

public class PluginManagerTest {
    
    private PluginManager plugins;
    
    @Before
    public void setUp() throws Exception {
        Session session = Util.getSession();
        Transaction tx = session.beginTransaction();
        session.createQuery("delete from BaseObject").executeUpdate();
        tx.commit();
        session.close();
        
        plugins = new PluginManager("org.rulez.magwas.worldmodel.BasicPlugin "
                + "org.rulez.magwas.worldmodel.HierarchyPlugin");
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test() {
        Session session = Util.getSession();
        
        try {
            plugins.init(session);
            plugins.checkConsistencyAll(session);
        } catch (Exception e) {
            session.close();
            e.printStackTrace();
            fail(e.getMessage());
        }
        session.close();
        assertEquals("org.rulez.magwas.worldmodel.PluginManager",
                plugins.getPluginName());
    }
    
}
