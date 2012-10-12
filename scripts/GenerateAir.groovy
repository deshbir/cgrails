import grails.util.GrailsUtil

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes


includeTargets << new File("${cgrailsPluginDir}/scripts/GenerateOffline.groovy")


target(air: "Generates Offline AIR version of the application") {
   depends(generate)
   grailsConsole.updateStatus "Started Generating AIR offline version.....";
   def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
   def airApplicationBuilder = applicationContext.getBean("airApplicationBuilder");
   grailsConsole.updateStatus "Cleaning older AIR package......";
   airApplicationBuilder.deleteOldPackage();
  
    grailsConsole.updateStatus "Successfully Cleaned older AIR package.....";
   grailsConsole.updateStatus "Creating AIR package......";
   
   airApplicationBuilder.generateAir("${cgrailsPluginDir}")
   
   grailsConsole.updateStatus "Successfully created AIR package.....";
   grailsConsole.updateStatus "AIR offline version successfully generated.....";
   
}

 
setDefaultTarget(air)
