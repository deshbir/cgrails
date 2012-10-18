includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsClean")
includeTargets << grailsScript("_GrailsCompile")

ant.project.getBuildListeners().each{
	if(it.metaClass.respondsTo(it, "setMessageOutputLevel")){
		it.setMessageOutputLevel(ant.project.MSG_INFO)
	}
}

target(sampleApp: "Creates a Cgrails sample application.....") {
	grailsConsole.updateStatus "Started creating Cgrails sample application"
	depends(addDecoratorMapper, addStyles, addControllers, addConfig, addJS, addViews, clean, compile)
	grailsConsole.updateStatus "Successfully created Cgrails sample application....."
}


target(addDecoratorMapper: "Installs CgrailsDecoratorMapper for sitemesh in application.") {
	grailsConsole.updateStatus "Started installing CgrailsDecoratorMapper for sitemesh....."
	def sitemeshXMLFile = "${basedir}/web-app/WEB-INF/sitemesh.xml"
	def sitemesh = new XmlParser().parse(sitemeshXMLFile)
	sitemesh."decorator-mappers"."mapper"[0].@class = "com.compro.cgrails.sitemesh.FallbackDecoratorMapper"
	new XmlNodePrinter(new PrintWriter(new FileWriter(sitemeshXMLFile))).print(sitemesh)
	grailsConsole.updateStatus "Successfully installed CgrailsDecoratorMapper for sitemesh....."
}

target(addStyles: "Adds required LESS files in the application.") {
	grailsConsole.updateStatus "Started adding LESS files....."
	ant.mkdir(dir: "web-app/css/cgrails/default/less")
	ant.copy(todir: "web-app/css/cgrails/default/less") {
		fileset(dir: "${cgrailsPluginDir}/default-app/css/default")
	}
	ant.mkdir(dir: "web-app/css/cgrails/skin1/less")
	ant.copy(todir: "web-app/css/cgrails/skin1/less") {
		fileset(dir: "${cgrailsPluginDir}/default-app/css/skin1")
	}
	grailsConsole.updateStatus "Successfully added LESS files....."
}
 
target(addControllers: "Adds required controllers in the application.") {
	grailsConsole.updateStatus "Started adding Controllers....."
	ant.mkdir(dir: "grails-app/controllers/com/compro/cgrails")
	ant.copy ( todir : 'grails-app/controllers/com/compro/cgrails'){
		fileset(dir: "${cgrailsPluginDir}/default-app/controllers/com/compro/cgrails")
	} 
	grailsConsole.updateStatus "Successfully added Controllers....."
}

target(addConfig: "Adds required config files in the application.") {
	grailsConsole.updateStatus "Started adding config files....."
	ant.copy(todir: "grails-app/conf") {
		fileset(dir: "${cgrailsPluginDir}/default-app/conf")
	}
	grailsConsole.updateStatus "Successfully added config files....."
}

target(addJS: "Adds required JS files in the application.") {
	grailsConsole.updateStatus "Started adding config JS files....."
	ant.copy(todir: "web-app/js") {
		fileset(dir: "${cgrailsPluginDir}/default-app/js")
	}
	grailsConsole.updateStatus "Successfully added JS files....."
}

target(addViews: "Adds required views in the application.") {
	grailsConsole.updateStatus "Started adding views....."
	ant.copy(todir: "grails-app/views") {
		fileset(dir: "${cgrailsPluginDir}/default-app/views")
	}
	grailsConsole.updateStatus "Successfully added views....."
}

setDefaultTarget(sampleApp)