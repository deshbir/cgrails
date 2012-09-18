import grails.util.GrailsUtil

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes



includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsRun")

includeTargets << new File("${cgrailsPluginDir}/scripts/DeployCss.groovy")

def classLoader = Thread.currentThread().contextClassLoader
classLoader.addURL(new File(classesDirPath).toURI().toURL())
def config = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('Config'))


target(run: "Runs the application") {
	depends(checkVersion, configureProxy, packageApp, parseArguments)
	runApp()
	//keepServerAlive()
}

target(generate: "Generates Offline version of the application") {
	String skin;
	if(argsMap.skin) {
		skin = argsMap.skin
		if(!config.cgrails.skinning.skins."${skin}") {
			ant.echo("********ERROR*************");
			ant.echo("Invalid Skin: " + skin + ". Please define skin in cgrails skinning configuration.");
			ant.echo("**************************");
			return
		}
	} else {
		skin = config.cgrails.skinning.baseskin
		argsMap.skin = skin
	}
	
	
   String pluginVersion = pluginSettings.getPluginInfo("${cgrailsPluginDir}").getVersion()
   depends(run)
   
   def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
   def offlineApplicationBuilder = applicationContext.getBean("offlineApplicationBuilder");
   
   ant.echo("****************************************************************************");
   ant.echo("*********** Started Generating offline version for " + skin + " skin.***********");
   //depends(deployCSS)
   ant.echo("Cleaning older package......");
   offlineApplicationBuilder.deleteOldPackage();
   ant.echo("Copying javascript files......");
   offlineApplicationBuilder.copyScripts("${cgrailsPluginDir}", pluginVersion);
   ant.echo("Copying image files......");
   offlineApplicationBuilder.copyImages();
   ant.echo("Copying CSS files......");
   offlineApplicationBuilder.copyStyles(skin);
   ant.echo("Creating Index HTML......");
   offlineApplicationBuilder.createIndex(skin);
   ant.echo("Creating preloaded templates file......");
   offlineApplicationBuilder.createPreloaderTemplate(skin, pluginVersion);
   ant.echo("Creating preloaded model file......");
   offlineApplicationBuilder.createPreloadedModel(pluginVersion);
   ant.echo("*********** Offline version successfully generated for " + skin + " skin.***********");
   ant.echo("****************************************************************************");
}

setDefaultTarget(generate)
