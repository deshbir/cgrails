TemplateManager =  {
	templates: {},
	url:"/" + com.compro.cgrails.APPLICATIONNAME + "/" + com.compro.cgrails.SKIN + "/" + com.compro.cgrails.WORKFLOW,
	get: function(id, callback,options){
		var cache;
		var templateUrl;
		if(!(options == undefined||options.url == null)){
			templateUrl = options.url;
		} else{
			templateUrl = this.url;
		}
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
	 			url: templateUrl + "/template/",
	 			type: 'POST',
		    	dataType: "html",
		    	data: { path: "templates/"+id},
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




