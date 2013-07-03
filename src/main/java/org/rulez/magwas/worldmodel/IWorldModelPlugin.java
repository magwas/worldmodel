package org.rulez.magwas.worldmodel;

import java.io.Serializable;

import org.hibernate.Session;

public interface IWorldModelPlugin extends Serializable {
    
    String getPluginName();
    
    /*
     * initialisation of the plugin
     */
    void init(Session session) throws Exception;
    
    /*
     * Checks the consistency of one object.
     * 
     * Called when all objects in the transaction are added.
     */
    
    void checkConsistency(Session session, BaseObject obj) throws Exception;
    
    /*
     * Adds computed fields if they are missing.
     * 
     * Called when all objects in the transaction are added.
     */
    
    void finalizeObject(Session session, BaseObject obj) throws Exception;
    
    /*
     * Checks the consistency of the whole database
     * 
     * Called rarely, by the administrator, maybe in command line?
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
