import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes


includeTargets << new File("${cgrailsPluginDir}/scripts/GenerateOffline.groovy")


target(appjs: "Generates Offline Appjs version of the application") {
   depends(generate)
   String pluginVersion = pluginSettings.getPluginInfo("${cgrailsPluginDir}").getVersion()
   grailsConsole.updateStatus "Started Generating Appjs offline version.....";
   def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
   def appjsApplicationBuilder = applicationContext.getBean("appjsApplicationBuilder");
   grailsConsole.updateStatus "Cleaning older Appjs package......";
   appjsApplicationBuilder.deleteOldPackage();
  
   grailsConsole.updateStatus "Successfully Cleaned older Appjs package.....";
   grailsConsole.updateStatus "Creating Appjs package......";
   
   appjsApplicationBuilder.generateAppjs("${cgrailsPluginDir}", pluginVersion, argsMap.debug)
   
   grailsConsole.updateStatus "Successfully created Appjs package.....";
   grailsConsole.updateStatus "Appjs offline version successfully generated.....";
   
}

 
setDefaultTarget(appjs)
