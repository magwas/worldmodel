var BaseObject = Class.extend({

	objects: {},

	create : function(id,type,source,dest,value) {
		var handler = this.getHandlerForType(type);
		//alert(handler);
		//alert(new handler());
		this.objects[id] = new handler(id,type,source,dest,value);	
	},
	
	fragToObject : function(of, klass) {
		//converts a BaseObject xml entity to object, using the klass constructor
		var type = of.getAttribute("type");
		var id = of.getAttribute("id");
		var source = of.getAttribute("source");
		var dest = of.getAttribute("dest");
		var value = of.getAttribute("value");
		this.objects[id] = new klass(id,type,source,dest,value);
	},
	
	getHandlerForType : function(type) {
		//FIXME: choose from different object handlers when there will be more
		return TableRowObject;
	},
	
	getObjectForId : function(id) {
		return this.objects[id];
	},
	
	fragToObjectAutomatic : function(of) {
		var type = of.getAttribute("type");
		this.fragToObject(of,this.getHandlerForType(type));
	},
	
	processResponse : function(response) {
		// processes a response from the server
		Util.processExceptions(response);
		var trs = response.querySelectorAll("BaseObject");
		for ( var i=0; i<trs.length;i++ ) {
			this.fragToObjectAutomatic(trs[i]);
		}
		var continues = response.querySelectorAll("continues").length;
		if(continues) {
			this.search(this.currentSearchQuery,this.currentSearchOffset+trs.length);
		}
	},
		
	addNodeAttr : function(node,fieldname) {
		// gathers the field value and sets it as the fieldname attribute of node
		var e = this.getDataFor(node,fieldname);
		if(e != "") {
			node.setAttribute(fieldname,e);
		}
	},

	submit : function() {
		 var doc = (new DOMParser()).parseFromString('<root id="root"/>', 'text/xml');
		 var root = doc.documentElement;
		 var bo = doc.createElement("BaseObject");
		 root.appendChild(bo);
		 this.addNodeAttr(bo,"id");
		 this.addNodeAttr(bo,"type");
		 this.addNodeAttr(bo,"source");
		 this.addNodeAttr(bo,"dest");
		 this.addNodeAttr(bo,"value");
		 var xmlText = new XMLSerializer().serializeToString(doc);
		 var response = Util.xmlPost(xmlText);
		 this.processResponse(response);
		 this.unedit();
	},
		
	search : function(query,offset) {//FIXME parameters changed!
		this.currentSearchQuery = query;
		this.currentSearchOffset = offset;
		if(offset != null ) {
			query += "&offset="+offset;
		}
		var response = Util.xmlGet(query);
		this.processResponse(response);
	},
});


