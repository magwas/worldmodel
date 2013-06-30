/*global define: false, DOMParser: false, checkalert: false */
require([ "thing/HierarchyTree", "thing/ObjectManager"
        ],
        function(HierarchyTree, ObjectManager) {
    test("init", function() {
        checkalert();
        var tt, thing, onOk, onBad, tester;
        checkalert();
        tt = new HierarchyTree();
        window.tt=tt;
        tester = function (obj,list) {
            onOk = function (l) {
                idlist = "";
                for(i=0; i<l.length; i++) {
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
                tester(ObjectManager.get("hierarchyroot"),
                        ",c1,ontology");
                
                tester(ObjectManager.get("hierarchy"),
                        ",c6,contains,c7,folder");
                var doc = (new DOMParser()).parseFromString(
                        '<object><BaseObject id="under_hierarchy" type="folder" source="hierarchy"/></object>', 'text/xml');
                ObjectManager.processResponse(doc);
                tester(ObjectManager.get("hierarchy"),
                ",c6,contains,c7,folder,under_hierarchy");
                string2 =        '<object>'+
                        '<BaseObject id="under_hierarchy2" type="folder"/>'+
                        '<BaseObject id="under_hierarchy2_rel" type="contains" source="hierarchy" dest="under_hierarchy2"/>'+
                        '</object>';
                //alert(string2);
                var doc2 = (new DOMParser()).parseFromString(string2, 'text/xml');
                ObjectManager.processResponse(doc2);
                checkalert();
                tester(ObjectManager.get("hierarchy"),
                ",c6,contains,c7,folder,under_hierarchy,under_hierarchy2_rel,under_hierarchy2");

            });
        },500);

    });

});
