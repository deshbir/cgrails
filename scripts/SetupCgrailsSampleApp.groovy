includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsClean")
includeTargets << grailsScript("_GrailsCompile")

ant.project.getBuildListeners().each{
	if(it.metaClass.respondsTo(it, "setMessageOutputLevel")){
		it.setMessageOutputLevel(ant.project.MSG_INFO)
	}
}

target(sampleApp: "Creates a Cgrails sample application.") {
	ant.echo("*****************Started creating Cgrails sample application**************")
	depends(addDecoratorMapper, addStyles, addControllers, addConfig, addJS, addViews, clean, compile)
	ant.echo("*****************Successfully created Cgrails sample application**************")
	ant.echo("****************************************************************************")
}


target(addDecoratorMapper: "Installs CgrailsDecoratorMapper for sitemesh in application.") {
	ant.echo("Started installing CgrailsDecoratorMapper for sitemesh........")
	def sitemeshXMLFile = "${basedir}/web-app/WEB-INF/sitemesh.xml"
	def sitemesh = new XmlParser().parse(sitemeshXMLFile)
	sitemesh."decorator-mappers"."mapper"[0].@class = "com.compro.cgrails.sitemesh.FallbackDecoratorMapper"
	new XmlNodePrinter(new PrintWriter(new FileWriter(sitemeshXMLFile))).print(sitemesh)
	ant.echo("Successfully installed CgrailsDecoratorMapper for sitemesh.")
}

target(addStyles: "Adds required LESS files in the application.") {
	ant.echo("Started adding LESS files........")
	ant.mkdir(dir: "web-app/css/cgrails/default/less")
	ant.copy(todir: "web-app/css/cgrails/default/less") {
		fileset(dir: "${cgrailsPluginDir}/default-app/css/default")
	}
	ant.mkdir(dir: "web-app/css/cgrails/skin1/less")
	ant.copy(todir: "web-app/css/cgrails/skin1/less") {
		fileset(dir: "${cgrailsPluginDir}/default-app/css/skin1")
	}
	ant.echo("Successfully added LESS files.")
}
 
target(addControllers: "Adds required controllers in the application.") {
	ant.echo("Started adding Controllers........")
	ant.mkdir(dir: "grails-app/controllers/com/compro/cgrails")
	ant.copy ( todir : 'grails-app/controllers/com/compro/cgrails' ,  file : "${cgrailsPluginDir}/default-app/controllers/com/compro/cgrails/MainController.groovy" )
	ant.echo("Successfully added Controllers.")
}

target(addConfig: "Adds required config files in the application.") {
	ant.echo("Started adding config files........")
	ant.copy(todir: "grails-app/conf") {
		fileset(dir: "${cgrailsPluginDir}/default-app/conf")
	}
	ant.echo("Successfully added config files.")
}

target(addJS: "Adds required JS files in the application.") {
	ant.echo("Started adding config JS files........")
	ant.copy(todir: "web-app/js") {
		fileset(dir: "${cgrailsPluginDir}/default-app/js")
	}
	ant.echo("Successfully added JS files.")
}

target(addViews: "Adds required views in the application.") {
	ant.echo("Started adding views........")
	ant.copy(todir: "grails-app/views") {
		fileset(dir: "${cgrailsPluginDir}/default-app/views")
	}
	ant.echo("Successfully added views.")
}

setDefaultTarget(sampleApp)