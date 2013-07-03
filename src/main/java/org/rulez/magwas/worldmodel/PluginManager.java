package org.rulez.magwas.worldmodel;

import java.util.ArrayList;

import org.hibernate.Session;

public class PluginManager implements IWorldModelPlugin {
    
    private static final long            serialVersionUID = 1L;
    private ArrayList<IWorldModelPlugin> pluginstack;
    
    public PluginManager(String pluginstackConfig)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        pluginstack = new ArrayList<IWorldModelPlugin>();
        for (String classname : pluginstackConfig.split("[ \t\n\r]+")) {
            Util.logInfo("initializing plugin:" + classname + ".");
            IWorldModelPlugin plugin = (IWorldModelPlugin) Class.forName(
                    classname).newInstance();
            pluginstack.add(plugin);
        }
        Util.logInfo("All plugins initialized.");
    }
    
    @Override
    public void init(Session session) throws Throwable {
        for (IWorldModelPlugin plugin : pluginstack) {
            plugin.init(session);
        }
    }
    
    @Override
    public void finalizeObject(Session session, BaseObject obj)
            throws Throwable {
        for (IWorldModelPlugin plugin : pluginstack) {
            plugin.finalizeObject(session, obj);
        }
    }
    
    @Override
    public void checkConsistencyAll(Session session) throws Throwable {
        for (IWorldModelPlugin plugin : pluginstack) {
            plugin.checkConsistencyAll(session);
        }
    }
    
    @Override
    public void checkConsistency(Session session, BaseObject obj)
            throws Throwable {
        for (IWorldModelPlugin plugin : pluginstack) {
            plugin.checkConsistencyAll(session);
        }
    }
    
    @Override
    public String getPluginName() {
        return this.getClass().getCanonicalName();
    }
    
}
