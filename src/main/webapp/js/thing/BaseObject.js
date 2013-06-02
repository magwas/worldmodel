define(["dojo/_base/declare","dojo/_base/lang"], function(declare, lang){
	var bo = declare("thing.BaseObject",null,{
		type : null,
		id : null,
		source : null,
		dest : null,
		value : null,
		name : null,
		// BaseObject, we don't know more about it
	   	constructor : function(attrs) {
			lang.mixin(this, attrs);
			this.name = "id="+this.id;
		},
		fragToObject : function(of) {
			//converts a xml entity to object
			var type = of.getAttribute("type");
			var id = of.getAttribute("id");
			var source = of.getAttribute("source");
			var dest = of.getAttribute("dest");
			var value = of.getAttribute("value");
			return new thing.BaseObject({id: id, type: type, source: source, dest: dest, value: value});
		}
	});
	bo.fragToObject = bo.prototype.fragToObject
	return bo;
});

