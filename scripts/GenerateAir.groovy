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
   ant.echo("Successfully Cleaned older AIR package.");
   ant.echo("Creating AIR package......");
   airApplicationBuilder.generateAir("${cgrailsPluginDir}")
   ant.echo("Successfully created AIR package.");
   ant.echo("***********AIR offline version successfully generated***************");
   ant.echo("****************************************************************************");
   
}

 
setDefaultTarget(air)
