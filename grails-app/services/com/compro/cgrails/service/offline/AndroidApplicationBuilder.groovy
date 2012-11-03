package  com.compro.cgrails.service.offline

import com.compro.cgrails.CgrailsConstants



class AndroidApplicationBuilder {
	
	def OfflineApplicationBuilder
	
	def grailsApplication
	
	private static final String ANDROID_DIR_PATH = "android/"
	private static final String ANDROID_TEMP_OFFLINE_DIR = "assets/offline/"
	private static final String ANDROID_CORODOVA_DIR = "cordova/"
	
	
	public void generateAndroid(String pluginDir,String pluginVersion) {
		boolean isCustomized = true;
		String androidDir = pluginDir + "/" + OfflineApplicationBuilder.OFFLINE_APP_DIR_PATH + ANDROID_DIR_PATH
		File offlineAndroidSource = new File(androidDir);
		
		String appOfflineAndroidSource = OfflineApplicationBuilder.TARGET_OFFLINE_DIR_PATH + ANDROID_DIR_PATH;
		File appOfflineAndroidSourceDir = new File(appOfflineAndroidSource);
		
		File offlineAndroidCorodovaDir = new File(appOfflineAndroidSource + ANDROID_CORODOVA_DIR);
		//System.out.println(appOfflineAndroidSource + ANDROID_CORODOVA_DIR + "*****************");
		//copy android resources to target/offine/android folder
		OfflineApplicationBuilder.copyDirectory(offlineAndroidSource, appOfflineAndroidSourceDir);
		
		File offlineCoreSource = new File(OfflineApplicationBuilder.OFFLINE_PACKAGE_DIR_PATH);
		File tempAndroidOffline = new File(appOfflineAndroidSource + ANDROID_TEMP_OFFLINE_DIR);
		
		//copy target/offline/core to target/offline/android/assets/offline 
		OfflineApplicationBuilder.copyDirectory(offlineCoreSource, tempAndroidOffline);
		
		File indexHtml = new File(appOfflineAndroidSource + ANDROID_TEMP_OFFLINE_DIR + "index.html")
		String content = readFile(indexHtml)
		String cordovaJS  = "<script type='text/javascript' src='cordova-2.1.0.js'></script>\n"
		int indexOfBody = content.indexOf("</body>")
		content = content.substring(0,indexOfBody)+ cordovaJS + content.substring(indexOfBody);
		writeFile(indexHtml, content)
		
		String[] command =  new String[3
			];
		command[0] = "cmd";
		command[1] = "/c";
		command[2] = "debug.bat";
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(offlineAndroidCorodovaDir);
		Process p = builder.start();
		BufferedReader stdInput = new BufferedReader(new
			  InputStreamReader(p.getInputStream()));
		String input;
		while ((input = stdInput.readLine()) != null) {
			  System.out.println(input)
		}

		BufferedReader stdError = new BufferedReader(new
			  InputStreamReader(p.getErrorStream()));

		String s = null;
		 // read any errors from the attempted command
		while ((s = stdError.readLine()) != null) {
			 System.out.println(s)
		}
	}
	
	public void deleteOldPackage() {
		File sourceFile = new File(OfflineApplicationBuilder.TARGET_OFFLINE_DIR_PATH + ANDROID_DIR_PATH)
		sourceFile.deleteDir();
	}
	
	public String readFile(File file) {
		FileInputStream fstream = new FileInputStream(file);
		// Get the object of DataInputStream
		DataInputStream din = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(din));
		String strLine;
		String content = "";
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
			content += "\n" + strLine;
		}
		//Close the input stream
		din.close();
		return content
	}
	public void writeFile(File file , String content){
		FileWriter fstream = new FileWriter(file);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(content);
		out.close();
	}


}
