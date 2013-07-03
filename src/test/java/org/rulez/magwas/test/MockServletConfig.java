package org.rulez.magwas.test;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class MockServletConfig implements ServletConfig {
    
    public String param;
    
    MockServletConfig() {
        super();
        param = "org.rulez.magwas.worldmodel.BasicPlugin org.rulez.magwas.worldmodel.HierarchyPlugin";
    }
    
    @Override
    public String getInitParameter(String arg0) {
        return param;
    }
    
    @Override
    public Enumeration<String> getInitParameterNames() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public ServletContext getServletContext() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getServletName() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
