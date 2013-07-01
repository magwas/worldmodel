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
                this.nameCallbacks = [];
                this.name = this.getName();
            },
            defname : function () {
                if (this.type && this.source && this.dest) {
                    var sourceobj, typeobj, destobj;
                    sourceobj = this.objectManager.get(this.source);
                    typeobj = this.objectManager.get(this.type);
                    destobj = this.objectManager.get(this.dest);
                    if(sourceobj && typeobj && destobj) {
                        this.name = "(" +
                        sourceobj.getName() + " " +
                        typeobj.getName() + " " +
                        destobj.getName() + ")";
                        typeobj.registerForNameChange(this);
                        sourceobj.registerForNameChange(this);
                        destobj.registerForNameChange(this);
                        
                    } else {
                        this.name = "(@" + this.id + ")";                        
                    }
                } else {
                    this.name = "(@" + this.id + ")";
                }
                if(this.value) {
                    this.name += "["+this.value+"]";
                }
                return this.name;
            },
            registerForNameChange: function (obj) {
                if( -1 ==this.nameCallbacks.indexOf(obj)) {
                    this.nameCallbacks.push(obj);                    
                }
            },
            flagNameChange: function () {
                if(this.nameCallbacks.length >0) {
                    for(var i = 0; i < this.nameCallbacks.length; i += 1) {
                        var ob = this.nameCallbacks[i];
                        ob.getName();
                    }                    
                }
            },
            getName : function () {
                var q, callback, self, oldname;
                self = this;
                oldname = self.name;
                self.defname();
                q = self.objectManager.query({type: 'name', source: self.id});
                callback = function(obj, removed, added) {
                    if(added > -1 ) {
                        var oldname = self.name;
                        self.name = obj.value;
                        obj.registerForNameChange(self);
                        if(oldname != self.name) {
                            self.flagNameChange();
                        }
                    }
                    if (removed > -1) {
                        self.defname();
                    }
                };
                q.observe(callback);
                if (q.length > 0) {
                    self.name = q[0].value;
                }
                //console.log("name for "+self.id+"="+self.name);
                if(oldname != self.name) {
                    this.flagNameChange();                    
                }
                return self.name;
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
