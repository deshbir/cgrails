import grails.util.GrailsUtil
import grails.util.Metadata

includeTargets << grailsScript("_GrailsInit")
eventPackagingEnd = {kind->	
	if (!(Metadata.getCurrent().getApplicationName().equals("cgrails"))) {
		includeTargets << new File("${cgrailsPluginDir}/scripts/DeployCss.groovy")
		includeTargets << new File("${cgrailsPluginDir}/scripts/ValidateApp.groovy")
		
		validateApp()
		
		def classLoader = Thread.currentThread().contextClassLoader
		classLoader.addURL(new File(classesDirPath).toURI().toURL())
		def config = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('CgrailsConfig'))
		if(!config.cgrails.cloudMode) {
			deployCSS()
		}	
	}
}


