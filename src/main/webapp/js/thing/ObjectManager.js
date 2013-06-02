define([
     	"dojo/store/Memory",
     	"dojo/store/Observable",
     	"dojo/_base/declare",
     	"dojo/_base/lang",
     	"thing/BaseObject"
  ], function(Memory,Observable,declare,lang,BaseObject){
	var om = Observable(new declare("thing.ObjectManager",Memory,{
		data: [],
		handlers: [],
	    getChildren: function(object) {
	       return this.query({parent: object.id});
	       },
		search : function(query,offset) {
			this.currentSearchQuery = query;
			this.currentSearchOffset = offset;
			if(offset != null ) {
				query += "&offset="+offset;
			}
			var response = Util.xmlGet(query);
			this.processResponse(response);
		},
		processResponse : function(response) {
			// processes a response from the server
			Util.processExceptions(response);
			var trs = response.querySelectorAll("BaseObject");
			for ( var i=0; i<trs.length;i++ ) {
				this.put(BaseObject.fragToObject(trs[i]),{overwrite: true});
			}
			var continues = response.querySelectorAll("continues").length;
			if(continues) {
				this.search(this.currentSearchQuery,this.currentSearchOffset+trs.length);
			}
		},
		getHandlerForType : function(type) {
			//FIXME: choose from different object handlers when there will be more
			return BaseObject;
		},
		submit : function(handlerobj) {
			 var doc = (new DOMParser()).parseFromString('<root id="root"/>', 'text/xml');
			 var root = doc.documentElement;
			 var bo = doc.createElement("BaseObject");
			 root.appendChild(bo);
			 this.addNodeAttr(handlerobj,bo,"id");
			 this.addNodeAttr(handlerobj,bo,"type");
			 this.addNodeAttr(handlerobj,bo,"source");
			 this.addNodeAttr(handlerobj,bo,"dest");
			 this.addNodeAttr(handlerobj,bo,"value");
			 var xmlText = new XMLSerializer().serializeToString(doc);
			 var response = Util.xmlPost(xmlText);
			 this.processResponse(response);
			 handlerobj.submitted();
		},
		addNodeAttr : function(handlerobj,node,fieldname) {
			var e = handlerobj.getDataFor(fieldname);
			if(e != "") {
				node.setAttribute(fieldname,e);
			}
		},
		getObjectForId : function(id) {
			var l = this.query({id: id})
			if(l.length > 1) { throw "internal error" }
			return l[0];
		},
		getChildren: function(object) {
			var q = this.query({type: object.id});
			if(q.length == 0) {
				q.observe();
				this.search("type="+object.id);
			}
			return q
		},
		create: function(id, type, src, dest, value) {
			this.put(new BaseObject({id: id, type: type, value: value, source: src, dest: dest}),{overwrite: true});			
		}
	})())
	dojo.ObjectManager = om;
	return om
});