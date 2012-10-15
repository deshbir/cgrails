modules = {	
	cgrailsLibs {
		resource url: [plugin: 'cgrails', dir: 'js/libs', file:'jquery-1.7.2.js']
		resource url: [plugin: 'cgrails', dir: 'js/libs', file:'underscore.js']
		resource url: [plugin: 'cgrails', dir: 'js/libs', file:'json2.js']
		resource url: [plugin: 'cgrails', dir: 'js/libs', file:'backbone.js']
		resource url: [plugin: 'cgrails', dir: 'js/libs', file:'templateManager.js']
	}
	offline {
		dependsOn 'cgrailsLibs'
		resource url: [plugin: 'cgrails', dir: 'js/offline/core', file:'backbone.localStorage.js']
		resource url: [plugin: 'cgrails', dir: 'js/offline/core', file:'preloaded_model.js']
		resource url: [plugin: 'cgrails', dir: 'js/offline/core', file:'preloaded_templates.js']
	}
	debugAir {
		dependsOn 'offline'
		resource url: [plugin: 'cgrails', dir: 'js/offline/air', file:'AIRIntrospector.js']
	}
	
}