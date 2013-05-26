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
		var value = of.innerHTML;
		this.objects[id] = new klass(id,type,source,dest,value);
	},
	
	getHandlerForType : function(type) {
		//FIXME: choose from different object handlers when there will be more
		return TableRowObject;
	},
	
	getObjectForId : function(id) {
		//FIXME: choose from different object handlers when there will be more
		return this.objects[id];
	},
	
	fragToObjectAutomatic : function(of) {
		var type = of.getAttribute("type");
		this.fragToObject(of,this.getHandlerForType(type));
	},
	
	processResponse : function(response) {
		// processes a response from the server
		if (Util.processExceptions(response)) {
			var trs = response.querySelectorAll("BaseObject");
			for ( var i=0; i<trs.length;i++ ) {
				this.fragToObjectAutomatic(trs[i]);
			}
		}
	},
	
	query : function(id) {
		response = Util.xmlGet("worldmodel?id="+this.id);
		var obfrags = response.getElementsByTagName("BaseObject");
		for ( var i=0; i<obfrags.length;i++ ) {
			var of = obfrags[i];
			this.fragToObjectAutomatic(of);
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
	},
	
	query : function(id) {
		response = Util.xmlGet("worldmodel?id="+id);
		this.processResponse(response);
	},
	
	search : function(type,id) {
		response = Util.xmlGet("search?"+type+"="+id);
		this.processResponse(response);
	},
},"singleton");


