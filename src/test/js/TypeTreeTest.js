require([
         "thing/TypeTree", "thing/BaseObject",
         ], function(TypeTree, BaseObject){
    test( "init", function() {
        var tt, thing, onOk, onBad, tester;
        checkalert();
        tt = new TypeTree();
        thing = BaseObject.objectManager.get("thing");
        equal("thing",thing.id);
        tester = function (obj,list) {
            onOk = function (l) {
                var idlist, i;
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
            test( "typehierarchytest", function() {
                tester(thing,
                        ",relation,folder,1_thing,1_relation,1_noninherited,1_noncircular,1_integer,1_document,1_tag,1_attribute,1_thing:2,1_relation:2,1_noninherited:2,1_noncircular:2,1_integer:2");
                
                tester(BaseObject.objectManager.get("relation"),
                        ",contains,1_quality,1_follows,1_contains,1_quality:2,1_follows:2");
                var doc = (new DOMParser()).parseFromString(
                        '<object><BaseObject id="under_relation" type="relation"/></object>', 'text/xml');
                BaseObject.objectManager.processResponse(doc);
                tester(BaseObject.objectManager.get("relation"),
                ",contains,1_quality,1_follows,1_contains,1_quality:2,1_follows:2,under_relation");

            });
        },500);
    });
});
