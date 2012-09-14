import grails.util.GrailsUtil

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes


includeTargets << new File("${cgrailsPluginDir}/scripts/GenerateOffline.groovy")


target(air: "Generates Offline AIR version of the application") {
   depends(generate)
   ant.echo("*********** Started Generating AIR offline version.***********");
   def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
   def airApplicationBuilder = applicationContext.getBean("airApplicationBuilder");
   ant.echo("Cleaning older AIR package......");
   airApplicationBuilder.deleteOldPackage();
   airApplicationBuilder.generateAir("${cgrailsPluginDir}")
   ant.echo("***********AIR offline version successfully generated***************");
   
}

 
setDefaultTarget(air)
