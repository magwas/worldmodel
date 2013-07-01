package org.rulez.magwas.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class IntegrationTestSelenium1 {
    private Selenium selenium;
    
    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*chrome",
                "http://tomcat.realm:8080/worldmodel/worldmodel");
        selenium.setSpeed("100");
        selenium.start();
    }
    
    @Test
    public void testIntegrationTestSelenium1() throws Exception {
        selenium.open("/worldmodel/worldmodel");
        Thread.sleep(1000);
        selenium.click("id=thing_but");
        selenium.type("id=thing_id_t", "instancias");
        selenium.type("id=thing_type_t", "folder");
        selenium.type("id=thing_source_t", "hierarchyroot");
        selenium.click("id=thing_but");
        selenium.click("//div[@id='dijit__TreeNode_2']/div/span");
    }
    
    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
