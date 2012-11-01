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
   grailsConsole.updateStatus "Generating zip file for Windows";
   appjsApplicationBuilder.generateWindowsAppjs("${cgrailsPluginDir}", pluginVersion)
   grailsConsole.updateStatus "Windows package for offline application succesfully zipped";
   grailsConsole.updateStatus "Generating zip file for Mac OS";
   appjsApplicationBuilder.generateMacAppjs("${cgrailsPluginDir}", pluginVersion)
   grailsConsole.updateStatus "Mac OS package for offline application succesfully zipped";
   grailsConsole.updateStatus "Successfully created Appjs package.....";
   grailsConsole.updateStatus "Appjs offline version successfully generated.....";
   
}

 
setDefaultTarget(appjs)
