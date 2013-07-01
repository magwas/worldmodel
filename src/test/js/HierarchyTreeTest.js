/*global define: false, DOMParser: false, checkalert: false */
require([ "thing/HierarchyTree", "thing/BaseObject"
        ],
        function(HierarchyTree, BaseObject) {
    test("init", function() {
        checkalert();
        var tt, thing, onOk, onBad, tester, string2;
        checkalert();
        tt = new HierarchyTree();
        tester = function (obj,list) {
            onOk = function (l) {
                var idlist, i;
                idlist = "";
                for( i=0; i<l.length; i++) {
                    idlist += ","+l[i].id;
                };
                equal(idlist,list);
            };
              onBad = function() {
              equal("should not be called","");  
            };
            tt.browser1.getChildren()[0].params.model.getChildren(obj,onOk,onBad);
            
        };
        setTimeout(function () {
            test( "hierarchytest", function() {
                tester(BaseObject.objectManager.get("hierarchyroot"),
                        ",c1,ontology");
                
                tester(BaseObject.objectManager.get("hierarchy"),
                        ",c6,contains,c7,folder");
                var doc = (new DOMParser()).parseFromString(
                        '<object><BaseObject id="under_hierarchy" type="folder" source="hierarchy"/></object>', 'text/xml');
                BaseObject.objectManager.processResponse(doc);
                tester(BaseObject.objectManager.get("hierarchy"),
                ",c6,contains,c7,folder,under_hierarchy");
                string2 =        '<object>'+
                        '<BaseObject id="under_hierarchy2" type="folder"/>'+
                        '<BaseObject id="under_hierarchy2_rel" type="contains" source="hierarchy" dest="under_hierarchy2"/>'+
                        '</object>';
                //alert(string2);
                var doc2 = (new DOMParser()).parseFromString(string2, 'text/xml');
                BaseObject.objectManager.processResponse(doc2);
                checkalert();
                tester(BaseObject.objectManager.get("hierarchy"),
                ",c6,contains,c7,folder,under_hierarchy,under_hierarchy2_rel,under_hierarchy2");

            });
        },500);

    });
    
    /* FIXME: failing test
    test("naming", function () {
        equal(document.getElementById("dijit__TreeNode_1").childNodes[0].childNodes[2].childNodes[2].innerHTML,
                "((@hierarchyroot) contains (@ontology))");
    });
    */

});
