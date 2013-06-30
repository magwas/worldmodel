/*global alert:false */

/**
 * Utility "class" for ajax/xslt related functionality not meant to be
 * instantiated
 */

var Util = function () {
    "use strict";
    alert("Util not meant to be instantiated!");
};

Util.xsltproc = null;

Util.xmlPost = function (xml) {
    "use strict";
    var xmlhttp, xmlDoc;
    xmlhttp = new XMLHttpRequest();
    xmlhttp.open("POST", "/worldmodel/worldmodel", false);
    xmlhttp.setRequestHeader("Content-type", "text/xml;charset=UTF-8");
    xmlhttp.send(xml);
    xmlDoc = xmlhttp.responseXML;
    return xmlDoc;

};

Util.xmlGet = function (uriend) {
    "use strict";
    var xmlhttp, xmlDoc;
    xmlhttp = new XMLHttpRequest();
    xmlhttp.open("GET", "/worldmodel/worldmodel?" + uriend, false);
    xmlhttp.setRequestHeader("Content-type", "text/xml;charset=UTF-8");
    xmlhttp.send();
    xmlDoc = xmlhttp.responseXML;
    return xmlDoc;
};

Util.showException = function (str) {
    alert(str);
};

Util.processExceptions = function (response) {
    "use strict";
    var allobjs, i;
    allobjs = response.getElementsByTagName("exception");
    for (i = 0; i < allobjs.length; i += 1) {
        Util.showException(allobjs[i].textContent);
    }
    return (allobjs.length === 0);
};
