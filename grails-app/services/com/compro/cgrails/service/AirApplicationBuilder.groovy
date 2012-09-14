package  com.compro.cgrails.service



class AirApplicationBuilder {
	
	def OfflineApplicationBuilder
	
	def grailsApplication
	
	private static final String AIR_DIR_PATH = "air/"
	private static final String AIR_TEMP_OFFLINE_DIR = "offline"
	
	
	public void generateAir(String pluginDir) {
		boolean isCustomized = true;
		File offlineAIRsource = new File(OfflineApplicationBuilder.OFFLINE_APP_DIR_PATH + AIR_DIR_PATH)
		if(!offlineAIRsource.exists()){
			isCustomized = false;
			String airDir = pluginDir + "/" + OfflineApplicationBuilder.OFFLINE_APP_DIR_PATH + AIR_DIR_PATH
			offlineAIRsource = new File(airDir);
		}
		String appOfflineAIRSource = OfflineApplicationBuilder.TARGET_OFFLINE_DIR_PATH + AIR_DIR_PATH;
		File appOfflineAIRSourceDir = new File(appOfflineAIRSource);
		
		//copy air resources to target/offine/air folder
		OfflineApplicationBuilder.copyDirectory(offlineAIRsource, appOfflineAIRSourceDir);
		
		File offlineCoreSource = new File(OfflineApplicationBuilder.OFFLINE_PACKAGE_DIR_PATH);
		File tempAirOffline = new File(appOfflineAIRSource + AIR_TEMP_OFFLINE_DIR);
		
		//copy target/offline/core to target/offline/air/offline 
		OfflineApplicationBuilder.copyDirectory(offlineCoreSource, tempAirOffline);
		
		def applicationName = grailsApplication.metadata['app.name']
		if (!isCustomized) {
			def appDescriptorFile = "${appOfflineAIRSourceDir}/app.xml"			
			def appRootElement = new XmlParser().parse(appDescriptorFile)
			appRootElement."filename"[0].setValue(applicationName)
			appRootElement."name"[0].setValue(applicationName)
			def nodePrinter = new XmlNodePrinter(new PrintWriter(new FileWriter(appDescriptorFile)))
			nodePrinter.preserveWhitespace = true
			nodePrinter.print(appRootElement)
		}	
		
		String exeName = "${applicationName}.exe"
		
		
		String[] command =  new String[5];
          command[0] = "cmd";
          command[1] = "/c";
          command[2] = "package-win.bat";
		  command[3] = exeName;
		  command[4] = AIR_TEMP_OFFLINE_DIR;
          ProcessBuilder builder = new ProcessBuilder(command);
          builder.directory(appOfflineAIRSourceDir);
		  Process p = builder.start();
		  BufferedReader stdInput = new BufferedReader(new
			  InputStreamReader(p.getInputStream()));

		 BufferedReader stdError = new BufferedReader(new
			  InputStreamReader(p.getErrorStream()));

		 String s = null;
		 // read any errors from the attempted command
		 while ((s = stdError.readLine()) != null) {
			 System.out.println(s);
		 }
		 
		 
		 tempAirOffline.deleteDir()
	}
	
	public void deleteOldPackage() {
		File sourceFile = new File(OfflineApplicationBuilder.TARGET_OFFLINE_DIR_PATH + AIR_DIR_PATH)
		sourceFile.deleteDir();
	}

}
