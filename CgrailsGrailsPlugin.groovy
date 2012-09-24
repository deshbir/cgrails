import grails.util.GrailsUtil

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
	}

	def doWithDynamicMethods = { ctx ->
		/**
		 * Overriding render function to add skinning fall back.
		 */
		application.controllerClasses.each { controller ->
			  def original = controller.metaClass.getMetaMethod("render", [Map] as Class[])
			  controller.metaClass.render = { Map args ->
					String baseDir = "/pages/"
					def classLoader = Thread.currentThread().contextClassLoader
					def cgrailsConfig = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('CgrailsConfig'))
					if(args.view) {
						def currentSkin = CgrailsUtils.getSkin()
						def viewPath = baseDir + currentSkin + "/" + args.view
						def fullViewPath= grailsAttributes.getViewUri(viewPath,request)
						def resource = grailsAttributes.getPagesTemplateEngine().getResourceForUri(fullViewPath)
						// if view does not exist in current skin , fall back to parent skin
						while (!resource.exists() && (currentSkin != cgrailsConfig.cgrails.skinning.baseskin)) {
							def parentSkin = cgrailsConfig.cgrails.skinning.skins."${currentSkin}".parent
							fullViewPath = fullViewPath.replaceFirst(currentSkin, parentSkin)
							resource = grailsAttributes.getPagesTemplateEngine().getResourceForUri(fullViewPath)
							currentSkin = parentSkin
						}
						args.view = baseDir + currentSkin + "/" + args.view
					}
					else if(args.template) {
						def currentSkin = CgrailsUtils.getSkin()
						def templatePath = baseDir + currentSkin + args.template
						def fullTemplatePath= grailsAttributes.getTemplateUri(templatePath,request)
						def resource = grailsAttributes.getPagesTemplateEngine().getResourceForUri(fullTemplatePath)
						// if template does not exist in current skin , fall back to parent skin
						while (!resource.exists() && (currentSkin != cgrailsConfig.cgrails.skinning.baseskin)) {
							def parentSkin = cgrailsConfig.cgrails.skinning.skins."${currentSkin}".parent
							fullTemplatePath = fullTemplatePath.replaceFirst(currentSkin, parentSkin)
							resource = grailsAttributes.getPagesTemplateEngine().getResourceForUri(fullTemplatePath)
							currentSkin = parentSkin
						}
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
