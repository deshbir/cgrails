import grails.util.GrailsUtil

import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.conn.HttpHostConnectException
import org.apache.http.impl.client.DefaultHttpClient
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
			grailsConsole.updateStatus "ERROR.....";
			grailsConsole.updateStatus "Invalid Skin: " + skin + ". Please define skin in cgrails skinning configuration.....";
			exit(1)
		}
	} else {
		skin = config.cgrails.skinning.defaultskin
		argsMap.skin = skin
	}	
	
	
   String pluginVersion = pluginSettings.getPluginInfo("${cgrailsPluginDir}").getVersion()
   depends(isServerRunning)
   def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
   def offlineApplicationBuilder = applicationContext.getBean("offlineApplicationBuilder");
   
   grailsConsole.updateStatus "Started Generating offline version for " + skin + " skin.....";
   //depends(deployCSS)
   grailsConsole.updateStatus "Cleaning older package.....";
   offlineApplicationBuilder.deleteOldPackage();
   grailsConsole.updateStatus "Successfully cleaned older package.....";
   grailsConsole.updateStatus "Copying javascript files.....";
   offlineApplicationBuilder.copyScripts("${cgrailsPluginDir}", pluginVersion);
   grailsConsole.updateStatus "Successfully copied javascript files.....";
   grailsConsole.updateStatus "Copying image files....." ;
   offlineApplicationBuilder.copyImages();
   grailsConsole.updateStatus "Successfully coped image files.....";
   grailsConsole.updateStatus "Copying CSS files.....";
   offlineApplicationBuilder.copyStyles(skin);
   grailsConsole.updateStatus "Successfully copied CSS files.....";
   grailsConsole.updateStatus "Creating Index HTML.....";
    offlineApplicationBuilder.createSinglepageHtmls(skin, argsMap.mode);
   grailsConsole.updateStatus "Successfully created Index HTML.....";
   grailsConsole.updateStatus "Creating preloaded templates file.....";
   offlineApplicationBuilder.createPreloaderTemplate(skin, pluginVersion);
   grailsConsole.updateStatus "Successfully created preloaded templates file.....";
   grailsConsole.updateStatus "Creating preloaded model file.....";
   offlineApplicationBuilder.createPreloadedModel(pluginVersion);
   grailsConsole.updateStatus "Successfully created preloaded model file.....";
   grailsConsole.updateStatus "Offline version successfully generated for " + skin + " skin.....";
}
target (isServerRunning : "Check if server is running") {
	
	def urlBuilder = new StringBuilder("http://");
	urlBuilder.append("localhost").append(":").append("8080").append("/");
		
	try{
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(urlBuilder.toString());
		
		HttpResponse response = httpclient.execute(httpGet);
		grailsConsole.updateStatus "Error: Server is running. Please stop the server and try again."
		exit(1);
	} catch(HttpHostConnectException e){
		//if the connection is refused run the server.
		depends(run)
	}
	catch (Exception e){
		grailsConsole.updateStatus e.message
		exit(1);
	}
	
}	

setDefaultTarget(generate)
