import com.compro.cgrails.CgrailsUtils


class CgrailsGrailsPlugin {
	// the plugin version
	def version = "1.0"
	// the version or versions of Grails the plugin is designed for
	def grailsVersion = "2.1 > *"
	// the other plugins this plugin depends on
	def dependsOn = ["resources": "1.2-RC1 > *"]
	def loadAfter = ['resources']
	// resources that are excluded from plugin packaging
	def pluginExcludes = [
		"grails-app/views/error.gsp"
	]

	// TODO Fill in these fields
	def title = "Cgrails Plugin" // Headline display name of the plugin
	def author = "Your name"
	def authorEmail = ""
	def description = '''\
Brief summary/description of the plugin.
'''

	// URL to the plugin's documentation
	def documentation = "http://grails.org/plugin/cgrails"

	// Extra (optional) plugin metadata

	// License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

	// Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

	// Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

	// Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

	// Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

	def doWithWebDescriptor = { xml ->
		// TODO Implement additions to web.xml (optional), this event occurs before
	}

	def doWithSpring = {
		// TODO Implement runtime spring config (optional)
		cgrailsService(com.compro.cgrails.service.DefaultCgrailsServiceImpl){
			it.autowire = true
		}
		offlineApplicationBuilder(com.compro.cgrails.service.OfflineApplicationBuilder){
			it.autowire = true
		}
		airApplicationBuilder(com.compro.cgrails.service.AirApplicationBuilder){
			it.autowire = true
		}
		androidApplicationBuilder(com.compro.cgrails.service.AndroidApplicationBuilder){
			it.autowire = true
		}
		appjsApplicationBuilder(com.compro.cgrails.service.AppjsApplicationBuilder){
			it.autowire = true
		}
	}

	def doWithDynamicMethods = { ctx ->
		/**
		 * Overriding render function to add skinning fall back.
		 */
		application.controllerClasses.each { controller ->
			  def original = controller.metaClass.getMetaMethod("render", [Map] as Class[])
			  def skinningService = ctx.getBean("skinningService")
			  controller.metaClass.render = { Map args ->
					String baseDir = "/pages/"
					def currentSkin = CgrailsUtils.getSkin()
					if(args.view) {
						def viewPath = baseDir + currentSkin + "/" + args.view
						def fullViewPath= grailsAttributes.getViewUri(viewPath,request)
						currentSkin = skinningService.getCalculatedSkinForResource(fullViewPath,currentSkin)
						args.view = baseDir + currentSkin + "/" + args.view
					}
					else if(args.template) {
						def templatePath = baseDir + currentSkin + args.template
						def fullTemplatePath= grailsAttributes.getTemplateUri(templatePath,request)
						currentSkin = skinningService.getCalculatedSkinForResource(fullTemplatePath,currentSkin)
						args.template = baseDir + currentSkin + "/" + args.template
					}
					original.invoke(delegate, args)
			  }
		}
	}

	def doWithApplicationContext = { applicationContext ->
		// TODO Implement post initialization spring config (optional)
	}

	def onChange = { event ->
		// TODO Implement code that is executed when any artefact that this plugin is
		// watching is modified and reloaded. The event contains: event.source,
		// event.application, event.manager, event.ctx, and event.plugin.
	}

	def onConfigChange = { event ->
		// TODO Implement code that is executed when the project configuration changes.
		// The event is the same as for 'onChange'.
	}

	def onShutdown = { event ->
		// TODO Implement code that is executed when the application shuts down (optional)
	}
}
