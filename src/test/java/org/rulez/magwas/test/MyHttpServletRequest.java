package org.rulez.magwas.test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;

import org.springframework.mock.web.MockHttpServletRequest;

class MyHttpServletRequest extends MockHttpServletRequest {
    public HashMap<String, String> parameterMap = new HashMap<String, String>();
    public String                  inputstring  = "no input";
    
    public void setParameter(String key, String value) {
        parameterMap.put(key, value);
    }
    
    public String getParameter(String arg0) {
        
        return parameterMap.get(arg0);
    }
    
    public void setInputString(String str) {
        inputstring = str;
    }
    
    public BufferedReader getReader() {
        return new BufferedReader(new StringReader(inputstring));
    }
    
}