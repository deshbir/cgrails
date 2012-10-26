import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes


includeTargets << new File("${cgrailsPluginDir}/scripts/GenerateOffline.groovy")


target(android: "Generates Android application") {
   
   depends(generate)
   String pluginVersion = pluginSettings.getPluginInfo("${cgrailsPluginDir}").getVersion()
   grailsConsole.updateStatus "Started Generating Android application.....";
   def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
   def androidApplicationBuilder = applicationContext.getBean("androidApplicationBuilder");
   grailsConsole.updateStatus "Cleaning older Android package......";
   androidApplicationBuilder.deleteOldPackage();
  
   grailsConsole.updateStatus "Successfully Cleaned older Android package.....";
   grailsConsole.updateStatus "Creating Android package......";
   
   androidApplicationBuilder.generateAndroid("${cgrailsPluginDir}", pluginVersion)
   
   grailsConsole.updateStatus "Successfully created Android Application.....";
}

 
setDefaultTarget(android)
