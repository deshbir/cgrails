import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes


includeTargets << new File("${cgrailsPluginDir}/scripts/GenerateOffline.groovy")


target(air: "Generates Offline AIR version of the application") {
	if(argsMap.debug.equals("true")){
		argsMap.mode = 'debugAir'
	}
   depends(generate)
   String pluginVersion = pluginSettings.getPluginInfo("${cgrailsPluginDir}").getVersion()
   grailsConsole.updateStatus "Started Generating AIR offline version.....";
   def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
   def airApplicationBuilder = applicationContext.getBean("airApplicationBuilder");
   grailsConsole.updateStatus "Cleaning older AIR package......";
   airApplicationBuilder.deleteOldPackage();
  
    grailsConsole.updateStatus "Successfully Cleaned older AIR package.....";
   grailsConsole.updateStatus "Creating AIR package......";
   
   airApplicationBuilder.generateAir("${cgrailsPluginDir}",pluginVersion,argsMap.debug)
   
   grailsConsole.updateStatus "Successfully created AIR package.....";
   grailsConsole.updateStatus "AIR offline version successfully generated.....";
   
}

 
setDefaultTarget(air)
