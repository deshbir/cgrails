TemplateManager =  {
	templates: {},
	get: function(id, callback,options){
		var cache;
		if(options == null){
			cache = true;
		} else if(options.cache == null){
			cache = true;
		} else {
			cache = options.cache;
		}
	 	var template = this.templates[id];
	 	if (template) {
	 		callback(template);
	 	} else if (com.compro.cgrails.WORKFLOW == "offline") {
	 		var template = com.compro.cgrails.OfflineTemplates[id];
	        callback(template);
	 	} else {
	 		var templateManagerRef = this;
	 		$.ajax({
	 			url: "/" + com.compro.cgrails.APPLICATIONNAME + "/" + com.compro.cgrails.SKIN + "/" + com.compro.cgrails.WORKFLOW + "/main/template/?path=templates/"+id,
		    	dataType: "html",
		        async: false,
		        success: function(template) {
		        	if(cache){				        		
		        		templateManagerRef.templates[id] = template;
		        	}
		 			callback(template);
		        }
		    });
	 	}
	},	
  	clearCache: function() {
		this.templates = {};
	}
};




