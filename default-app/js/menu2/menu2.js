View2 = new function() {

	var router = null;
	
	var Router = Backbone.Router.extend({
		routes: {
	      'template2':'template2'
	    },	    
	    template2 : function() {
	    	View2.initialize()
	    }
	});
	this.initialize = function(){
		if (router == null) {
			router = new Router();
		}
		TemplateManager.get('template2', function(template){
			$("#main_container").html(template);
		 }); 
	};
	this.routerInitialize = function(){
		router = new Router();   
	};
};