package  com.compro.cgrails.service

import com.compro.cgrails.CgrailsConstants



class AppjsApplicationBuilder {
	
	def OfflineApplicationBuilder
	
	def grailsApplication
	
	private static final String APP_JS_DIR_PATH = "appjs/"
	private static final String APP_JS_CONTENT_DIR = "content"
	
	
	public void generateAppjs(String pluginDir,String pluginVersion, String debug) {
		File offlineSource = new File(pluginDir + "/" + OfflineApplicationBuilder.OFFLINE_APP_DIR_PATH + APP_JS_DIR_PATH)
		File appjsTargetDir = new File(OfflineApplicationBuilder.TARGET_OFFLINE_DIR_PATH + APP_JS_DIR_PATH);
		
		//copy air resources to target/offine/appjs folder
		OfflineApplicationBuilder.copyDirectory(offlineSource, appjsTargetDir);
		
		File offlineCoreSource = new File(OfflineApplicationBuilder.OFFLINE_PACKAGE_DIR_PATH);
		File appjsContentDir = new File(appjsTargetDir.getPath() + "/data/" + APP_JS_CONTENT_DIR);
		
		//copy target/offline/core to target/offline/air/offline 
		OfflineApplicationBuilder.copyDirectory(offlineCoreSource, appjsContentDir);
				
	}
	
	public void deleteOldPackage() {
		File sourceFile = new File(OfflineApplicationBuilder.TARGET_OFFLINE_DIR_PATH + APP_JS_DIR_PATH)
		sourceFile.deleteDir();
	}

}
