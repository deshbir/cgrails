import grails.util.GrailsUtil

includeTargets << grailsScript("_GrailsInit")
eventCompileEnd = {kind->	
	def classLoader = Thread.currentThread().contextClassLoader
	classLoader.addURL(new File(classesDirPath).toURI().toURL())
	def config = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('Config'))
	if (!(config.grails?.resources?.debug == null || config.grails.resources.debug == true)) {
		includeTargets << new File("${cgrailsPluginDir}/scripts/DeployCss.groovy")
		deployCSS()
	}
}


