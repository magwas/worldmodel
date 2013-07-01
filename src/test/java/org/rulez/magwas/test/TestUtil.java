package org.rulez.magwas.test;

public class TestUtil {
    
    public static String NormalizeXmlString(String in) {// FIXME testcases for
                                                        // stylesheet and xml
                                                        // header
        return in
                .replaceAll("<.xml version=\"1.0\" encoding=\"UTF-8\".*?>", "")
                .replaceAll("\n", "")
                .replace(
                        "<?xml-stylesheet type=\"text/xsl\" href=\"stylesheet.xsl\"?>",
                        "");
    }
    
}
