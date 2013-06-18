/*global define: false, thing: false */
define([ "dojo/_base/declare", "dojo/_base/lang" ], function (declare, lang) {
    "use strict";
    var bo = declare("thing.BaseObject", null, {
        type : null,
        id : null,
        source : null,
        dest : null,
        value : null,
        name : null,
        // BaseObject, we don't know more about it
        constructor : function (attrs) {
            lang.mixin(this, attrs);
            this.name = "id=" + this.id;
        },
        fragToObject : function (of) {
            // converts a xml entity to object
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
    return bo;
});
