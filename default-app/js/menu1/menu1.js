View1 = new function() {

	var router = null;
	
	var Router = Backbone.Router.extend({
		routes: {
	      'template1':'template1'
	    },	    
	    template1 : function() {
	    	View1.initialize()
	    }
	});
	this.initialize = function(){
		if (router == null) {
			router = new Router();
		}
		TemplateManager.get('template1', function(template){
			$("#main_container").html(template);
		 }); 
	};
	this.routerInitialize = function(){
		router = new Router();   
	};
};