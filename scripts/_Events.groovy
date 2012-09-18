import grails.util.GrailsUtil

includeTargets << grailsScript("_GrailsInit")
eventPackagingEnd = {kind->	
	def classLoader = Thread.currentThread().contextClassLoader
	classLoader.addURL(new File(classesDirPath).toURI().toURL())
	def config = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('Config'))
	if (!(config.cgrails.less.deploycss == false)) {
		includeTargets << new File("${cgrailsPluginDir}/scripts/DeployCss.groovy")
		deployCSS()
	}
}


