/*global define: false, thing: false */
define([ "dojo/_base/declare", "dojo/_base/lang",
         "thing/ObjectManager", "dojo/store/Observable"],
    function (declare, lang, ObjectManager, observable) {
        "use strict";
        var bo = declare("thing.BaseObject", null, {
            type : null,
            id : null,
            source : null,
            dest : null,
            value : null,
            name : null,
            constructor : function (attrs) {
                lang.mixin(this, attrs);
                this.name = this.getName();
            },
            defname : function () {
                this.name = this.id;
                return this.name;
            },
            getName : function () {
                if (this.source && this.dest && this.type) {
                    var sourceobj, destobj, typeobj;
                    sourceobj = this.objectManager.get(this.source);
                    destobj = this.objectManager.get(this.dest);
                    typeobj = this.objectManager.get(this.type);
                    if (sourceobj && destobj && typeobj) {
                        this.name = "(" +
                            sourceobj.getName() + " " +
                            typeobj.getName() + " " +
                            destobj.getName() + ")";
                    } else {
                        this.defname();
                    }
                } else {
                    this.defname();
                }
                return this.name;
            },
            fragToObject : function (of) {
                //  converts a xml entity to object
                return new thing.BaseObject({
                    id : of.getAttribute("id"),
                    type : of.getAttribute("type"),
                    source : of.getAttribute("source"),
                    dest : of.getAttribute("dest"),
                    value : of.getAttribute("value")
                });
            }
        });
        bo.fragToObject = bo.prototype.fragToObject;
        bo.prototype.objectManager = observable(new ObjectManager(bo));
        bo.objectManager = bo.prototype.objectManager;
        return bo;
    });
