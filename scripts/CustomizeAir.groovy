import grails.util.GrailsUtil

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes


includeTargets << new File("${cgrailsPluginDir}/scripts/GenerateOffline.groovy")

target(customAir: "Copies AIR resources to application to enable User to customize AIR offline version generation.") {
	ant.echo("****************************************************************************");
	ant.echo("*********** Started copying AIR resources to application.***********");
	ant.delete(dir: "offline-app/air")
	ant.mkdir(dir: "offline-app/air")
	ant.copy(todir: "offline-app/air") {	 
	  fileset(dir: "${cgrailsPluginDir}/offline-app/air")
	}
	ant.echo("***********Successfully copied AIR resources to application.***************");
 }
 
setDefaultTarget(customAir)
