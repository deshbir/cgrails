import grails.util.GrailsUtil
import grails.util.Metadata

includeTargets << grailsScript("_GrailsInit")
eventPackagingEnd = {kind->	
	if (!(Metadata.getCurrent().getApplicationName().equals("cgrails"))) {
		includeTargets << new File("${cgrailsPluginDir}/scripts/DeployCss.groovy")
		includeTargets << new File("${cgrailsPluginDir}/scripts/ValidateApp.groovy")
		validateApp()
		deployCSS()
	}
}


