/*global define: false, Util: false, DOMParser: false, XMLSerializer: false */
define([ "dojo/store/Memory", "dojo/store/Observable", "dojo/_base/declare",
        "dojo/_base/lang", "thing/BaseObject" ], function (Memory, observable,
        declare, lang, BaseObject) {
    "use strict";
    var ObjectManagerClass, om;
    ObjectManagerClass = declare("thing.ObjectManager", Memory, {
        data : [],
        handlers : [],
        search : function (query, offset) {
            this.currentSearchQuery = query;
            this.currentSearchOffset = offset;
            if (offset !== undefined) {
                query += "&offset=" + offset;
            }
            var response = Util.xmlGet(query);
            this.processResponse(response);
        },
        processResponse : function (response) {
            // processes a response from the server
            var trs, i, continues;
            Util.processExceptions(response);
            trs = response.querySelectorAll("BaseObject");
            for (i = 0; i < trs.length; i += 1) {
                this.put(BaseObject.fragToObject(trs[i]), {
                    overwrite : true
                });
            }
            continues = response.querySelectorAll("continues").length;
            if (continues) {
                this.search(this.currentSearchQuery, this.currentSearchOffset
                        + trs.length);
            }
        },
        getHandlerForType : function (type) { // FIXME unit test
            // FIXME: choose from different object handlers when there will be
            // more
            return BaseObject;
        },
        submit : function (handlerobj) {
            var doc, root, bo, xmlText, response;
            doc = (new DOMParser()).parseFromString('<root id="root"/>',
                    'text/xml');
            root = doc.documentElement;
            bo = doc.createElement("BaseObject");
            root.appendChild(bo);
            this.addNodeAttr(handlerobj, bo, "id");
            this.addNodeAttr(handlerobj, bo, "type");
            this.addNodeAttr(handlerobj, bo, "source");
            this.addNodeAttr(handlerobj, bo, "dest");
            this.addNodeAttr(handlerobj, bo, "value");
            xmlText = new XMLSerializer().serializeToString(doc);
            response = Util.xmlPost(xmlText);
            this.processResponse(response);
            handlerobj.submitted();
        },
        /* private */
        addNodeAttr : function (handlerobj, node, fieldname) {
            var e = handlerobj.getDataFor(fieldname);
            if (e !== "") {
                node.setAttribute(fieldname, e);
            }
        },
        getObjectForId : function (id) {
            var l = this.query({
                id : id
            });
            if (l.length > 1) {
                throw "internal error";
            }
            return l[0];
        },
        getChildren : function (object) { // FIXME unit test
            var q = this.query({
                type : object.id
            });
            if (q.length === 0) {
                q.observe();
                this.search("type=" + object.id);
            }
            return q;
        },
        create : function (id, type, src, dest, value) {// FIXME unit test
            this.put(new BaseObject({
                id : id,
                type : type,
                value : value,
                source : src,
                dest : dest
            }), {
                overwrite : true
            });
        }
    });
    om = observable(new ObjectManagerClass());
    //dojo.ObjectManager = om;
    return om;
});
