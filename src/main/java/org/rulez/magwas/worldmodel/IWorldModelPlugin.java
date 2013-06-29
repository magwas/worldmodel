package org.rulez.magwas.worldmodel;

import org.hibernate.Session;

public interface IWorldModelPlugin {
    
    /*
     * initialisation of the plugin
     */
    public void init(Session session) throws Exception;
    
    /*
     * Checks the consistency of one object.
     * 
     * Called when all objects in the transaction are added
     */
    
    void checkConsistencyOne(Session session, BaseObject obj) throws Exception;
    
    /*
     * Checks the consistency of the whole database
     * 
     * Called rarely, by the administrator
     */
    
    // FIXME not called
    void checkConsistencyAll(Session session) throws Exception;
    
    /*
     * returns the list of names of supported elements.
     */
    // FIXME String[] getSupportedElements();
    // FIXME a mechanism to add xslt for input and output transformations
    // FIXME a mechanism to contribute to xsd
    // FIXME a mechanism to add xpath functions
    
}
