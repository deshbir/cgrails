import grails.util.GrailsUtil

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes



includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsRun")

includeTargets << new File("${cgrailsPluginDir}/scripts/DeployCss.groovy")


target(run: "Runs the application") {
	depends(checkVersion, configureProxy, packageApp, parseArguments)
	runApp()
	//keepServerAlive()
}

target(generate: "Generates Offline version of the application") {
	
	def classLoader = Thread.currentThread().contextClassLoader
	classLoader.addURL(new File(classesDirPath).toURI().toURL())
	def config = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('CgrailsConfig'))
	String skin;
	if(argsMap.skin) {
		skin = argsMap.skin
		if(!config.cgrails.skinning.skins."${skin}") {
			ant.echo("********ERROR*************");
			ant.echo("Invalid Skin: " + skin + ". Please define skin in cgrails skinning configuration.");
			ant.echo("**************************");
			exit(1)
		}
	} else {
		skin = config.cgrails.skinning.baseskin
		argsMap.skin = skin
	}	
	
	
   String pluginVersion = pluginSettings.getPluginInfo("${cgrailsPluginDir}").getVersion()
   depends(run)
   
   def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
   def offlineApplicationBuilder = applicationContext.getBean("offlineApplicationBuilder");
   
   ant.echo("*********** Started Generating offline version for " + skin + " skin.***********");
   //depends(deployCSS)
   ant.echo("Cleaning older package......");
   offlineApplicationBuilder.deleteOldPackage();
   ant.echo("Successfully cleaned older package.");
   ant.echo("Copying javascript files......");
   offlineApplicationBuilder.copyScripts("${cgrailsPluginDir}", pluginVersion);
   ant.echo("Successfully copied javascript files.");
   ant.echo("Copying image files......");
   offlineApplicationBuilder.copyImages();
   ant.echo("Successfully coped image files.");
   ant.echo("Copying CSS files......");
   offlineApplicationBuilder.copyStyles(skin);
   ant.echo("Successfully copied CSS files.");
   ant.echo("Creating Index HTML......");
   offlineApplicationBuilder.createIndex(skin);
   ant.echo("Successfully created Index HTML.");
   ant.echo("Creating preloaded templates file......");
   offlineApplicationBuilder.createPreloaderTemplate(skin, pluginVersion);
   ant.echo("Successfully created preloaded templates file.");
   ant.echo("Creating preloaded model file......");
   offlineApplicationBuilder.createPreloadedModel(pluginVersion);
   ant.echo("Successfully created preloaded model file.");
   ant.echo("*********** Offline version successfully generated for " + skin + " skin.***********");
   ant.echo("****************************************************************************");
}

setDefaultTarget(generate)
