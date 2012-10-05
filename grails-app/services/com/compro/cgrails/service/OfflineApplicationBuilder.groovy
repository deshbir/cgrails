package  com.compro.cgrails.service


import grails.converters.JSON
import grails.util.GrailsUtil
import net.sf.json.JSONObject

import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair

import com.compro.cgrails.CgrailsConstants

class OfflineApplicationBuilder {
	
	static final String TARGET_OFFLINE_DIR_PATH = "target/offline/"
	private static final String OFFLINE_CORE_DIR_PATH = "core/"
	static final String OFFLINE_PACKAGE_DIR_PATH = TARGET_OFFLINE_DIR_PATH + OFFLINE_CORE_DIR_PATH	
	static final String OFFLINE_APP_DIR_PATH = "offline-app/"	
	private static final String WEBAPP_DIR_NAME = "web-app"	
	private static final String PAGES_DIR_PATH = "grails-app/views/pages/"	
	private static final String JAVSCRIPT_DIR_NAME = "js"
	private static final String IMAGES_DIR_NAME = "images"
	private static final String FONTS_DIR_NAME = "fonts"
	private static final String CSS_DIR_NAME = "css"
	private static final String INDEX_FILE_NAME = "index.html"	
	private static final String CGRAILS_CONFIG_FILE_NAME = "CgrailsConfig"
	private static final String TEMPLATES_FOLDER_NAME = "templates"	
	private static final String PRELOADED_TEMPLATES_JS_PATH = "/offline/preloaded_templates.js"
	private static final String PRELOADED_MODELS_JS_PATH = "/offline/preloaded_model.js"	
	private static final String APP_HOST = "localhost"
	private static final String APP_PORT = "8080"

	
	
	def grailsApplication
	
	private static FileFilter cgrailsFileFilter = new FileFilter() {
		public boolean accept(File file) {
			return !file.getName().equals(CgrailsConstants.CGRAILS);
		}
	}
	
	private static FileFilter svnFileFilter = new FileFilter() {
		public boolean accept(File file) {
			return !file.getName().contains(".svn");
		}
	}

	private static FileFilter lessFileFilter = new FileFilter() {
		public boolean accept(File file) {
			return !file.getName().equals(CgrailsConstants.LESS_FOLDER_NAME);
		}
	}
	
	public void copyScripts(String pluginDir, String pluginVersion) {
		
		//Copying offline Javascripts required from plugin
		File offlineJSsource = new File(pluginDir + "/" + WEBAPP_DIR_NAME + "/" +JAVSCRIPT_DIR_NAME + "/offline");
			
		File targetOfflineJSfolder = new File(OFFLINE_PACKAGE_DIR_PATH + "plugins/" +
			CgrailsConstants.CGRAILS + "-" + pluginVersion + "/" + JAVSCRIPT_DIR_NAME + "/offline");
		copyDirectory(offlineJSsource, targetOfflineJSfolder);
		
		//Copying libs Javascripts required from plugin
		File cgrailsLibsJSsource = new File(pluginDir + "/" + WEBAPP_DIR_NAME + "/" + JAVSCRIPT_DIR_NAME + "/libs");
		File targetLibsJSfolder = new File(OFFLINE_PACKAGE_DIR_PATH + "plugins/" +
			CgrailsConstants.CGRAILS + "-" + pluginVersion + "/" + JAVSCRIPT_DIR_NAME + "/libs");
		copyDirectory(cgrailsLibsJSsource, targetLibsJSfolder);
		
		File src = new File(WEBAPP_DIR_NAME + "/" + JAVSCRIPT_DIR_NAME);
		File dst = new File(OFFLINE_PACKAGE_DIR_PATH + JAVSCRIPT_DIR_NAME);
		copyDirectory(src, dst);
	}
	
	public void copyImages() {
		File src = new File(WEBAPP_DIR_NAME + "/" + IMAGES_DIR_NAME);
		File dst = new File(OFFLINE_PACKAGE_DIR_PATH + IMAGES_DIR_NAME);
		copyDirectory(src, dst);
	}
	
	private void createPreloadedModel(String pluginVersion) {
		def classLoader = Thread.currentThread().contextClassLoader
		def config = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass(CGRAILS_CONFIG_FILE_NAME))
		String javascriptmvc = config.cgrails.javascriptMVC
		if(javascriptmvc == null || javascriptmvc == "backbone") {
			createBacbonePreloadedModel(pluginVersion);
		}				
	}
	
	private void createBacbonePreloadedModel(String pluginVersion) {
		
		def modelMainBuffer = new StringBuffer();
		def modelDataBuffer = new StringBuffer();
		def modelLoaderBuffer = new StringBuffer();
		
		modelMainBuffer.append("com.compro.cgrails.PreloadedModel = new function () {").append("\n");
		
		//Get all domain classes of application.
		def domainClasses = grailsApplication.domainClasses
		for (def domainClass:domainClasses) {
			Class domain = domainClass.clazz
			//getting class name.
			String modelClassName = domain.getName()
			String backboneObject, backboneType
			JSON initialJsonData
			try {
				// getting backboneObject property
				backboneObject = domain.backboneObject
			} catch (MissingPropertyException e) {
			    // If backboneObject property not found, do nothing. Continue to next item
				continue
			}			
			try {	
				// getting backboneType property
				backboneType = domain.backboneType
			} catch (MissingPropertyException e) {
				// If backboneType property not found, do nothing. Continue to next item
				continue
			}
			try {
				// call initialData function to get pre-loaded data.
				initialJsonData = domain.initialData()
			} catch (MissingMethodException e) {
				// If initialData function not found, do nothing. Continue to next item
				continue
			}	
			String dataString = modelClassName.substring(modelClassName.lastIndexOf(".") + 1, modelClassName.length()).toLowerCase() + "Data"
			modelDataBuffer.append("var ").append(dataString).append(" = ").append(initialJsonData);
			
			if(backboneType == "model") {
				modelDataBuffer.append(";").append("\n");
				modelLoaderBuffer.append(backboneObject).append(".get().save(").append(dataString).append(")").append("\n");
			} else if (backboneType == "collection") {
				modelDataBuffer.append("\n");
				modelLoaderBuffer.append("for (var i=0; i<").append(dataString).append(".length;i++) {").append("\n");
				modelLoaderBuffer.append(backboneObject).append(".get().create(").append(dataString).append("[i])").append("\n");
				modelLoaderBuffer.append("}").append("\n");
			}
		}
		modelMainBuffer.append(modelDataBuffer).append("\n");
		modelMainBuffer.append("this.load = function () {").append("\n");
		modelMainBuffer.append(modelLoaderBuffer);
		modelMainBuffer.append("};").append("\n");
		modelMainBuffer.append("}");
		
								
		File file = new File(OFFLINE_PACKAGE_DIR_PATH + "plugins/" + CgrailsConstants.CGRAILS
								+ "-" + pluginVersion + "/" + JAVSCRIPT_DIR_NAME + PRELOADED_MODELS_JS_PATH);
		
		//Add content to preloaded_model.js
		file.write(modelMainBuffer.toString());
	}
	private void copyStyles(String skin) {
		/****************************************************
		 * STEP 1: Copy everything except cgrails folder.
		 ****************************************************/
		File src = new File(WEBAPP_DIR_NAME + "/" + CSS_DIR_NAME);
		File dst = new File(OFFLINE_PACKAGE_DIR_PATH + CSS_DIR_NAME);
		File[] files = src.listFiles(cgrailsFileFilter);
		for(File file :  files){
			File destFile = new File(OFFLINE_PACKAGE_DIR_PATH + CSS_DIR_NAME + "/" + file.getName());
			copyDirectory(file, destFile);
		}
		/*****************************************
		 * STEP 2: Copy only skin specific styles from cgrails folder(do not copy less files).
		 ***************************************/
		File skinDir = new File(WEBAPP_DIR_NAME + "/" + CSS_DIR_NAME + "/" + CgrailsConstants.CGRAILS + "/" + skin);
		File[] skinFileSrc = skinDir.listFiles(lessFileFilter);
		for(File file :  skinFileSrc){
			File skinFileDest = new File(OFFLINE_PACKAGE_DIR_PATH + CSS_DIR_NAME + "/" + CgrailsConstants.CGRAILS + "/" + skin +
				"/" + file.getName());
			copyDirectory(file, skinFileDest);
		}
	}

	
	public void createIndex(String skin) {
		def urlBuilder = new StringBuilder("http://");
		urlBuilder.append(APP_HOST).append(":").append(APP_PORT).append("/").append(grailsApplication.metadata['app.name']);
		urlBuilder.append("/").append(skin).append("/").append(CgrailsConstants.WORKFLOW_OFFLINE).append("/?_offlineMode=y");
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(urlBuilder.toString());
		HttpResponse response = httpClient.execute(getRequest);
		
		String html = getResponseAsString(response, urlBuilder.toString());
		
		InputStream contentStringStream = new ByteArrayInputStream(html.getBytes());
		writeFile(contentStringStream, new File(OFFLINE_PACKAGE_DIR_PATH + INDEX_FILE_NAME));
	}
	
	private Set<String> getRequiredTemplates(String skin, def config){
		Set<String> templateList = new HashSet<String>();
		getTemplatesInSkin(config,skin,templateList)
		return templateList;
	}
	private void getTemplatesInSkin(def config, String currentSkin, Set<String> templateList){
		if(currentSkin != config.cgrails.skinning.baseskin){
			String parentSkin = config.cgrails.skinning.skins."${currentSkin}".parent
			getTemplatesInSkin(config, parentSkin,templateList);
			File templateDir  = new File(PAGES_DIR_PATH + currentSkin + "/" + TEMPLATES_FOLDER_NAME);
			getFileList(templateDir, templateList);
		} else {
			File templateDir  = new File(PAGES_DIR_PATH + currentSkin + "/" + TEMPLATES_FOLDER_NAME);
			getFileList(templateDir, templateList);
		}
	}
	private void getFileList(File dir, Set<String> fileList) {
		File[] children = dir.listFiles(svnFileFilter);
		for (File file : children) {
			if (file.isDirectory()) {
				getFileList(file,fileList);
			} else {
				String templatesPath = TEMPLATES_FOLDER_NAME + "\\"
				String path = file.getPath();
				path = path.substring(path.indexOf(templatesPath) + templatesPath.length());
				path = path.substring(0, path.indexOf(".gsp"));
				path = path.replace("\\", "/");
				fileList.add(path);
			}
		}
	}
		
	
	public void createPreloaderTemplate(String skin, String pluginVersion) {
		def classLoader = Thread.currentThread().contextClassLoader
		def config = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass(CGRAILS_CONFIG_FILE_NAME))
		Boolean isConfigurable = config.cgrails.templates.useConfiguration
		Set<String> templateList
		if(isConfigurable) {
			def offlineConf = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass(CGRAILS_CONFIG_FILE_NAME))
			templateList = new HashSet(offlineConf.cgrails.templateList);
		} else {
			templateList = getRequiredTemplates(skin, config);
		}
		JSONObject jsonTemplate = new JSONObject();
		String template;
		for (String templatename :  templateList) {
			def urlBuilder = new StringBuilder("http://");
			urlBuilder.append(APP_HOST).append(":").append(APP_PORT).append("/")
				.append(grailsApplication.metadata['app.name']).append("/cgrailstemplate/?_offlineMode=y");
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(urlBuilder.toString());
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("skin", skin));
			nameValuePairs.add(new BasicNameValuePair("workflow", CgrailsConstants.WORKFLOW_OFFLINE));
			nameValuePairs.add(new BasicNameValuePair("path", TEMPLATES_FOLDER_NAME + "/" + templatename));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			HttpResponse response = httpclient.execute(httppost);			
			
			template = getResponseAsString(response, urlBuilder.toString());			
			template = template.replace("\r\n", "").replace("\t", "");
			jsonTemplate.put(templatename, template);
		}
		String contentString = "com.compro.cgrails.OfflineTemplates = " + jsonTemplate.toString(8);
		InputStream contentStringStream = new ByteArrayInputStream(contentString.getBytes());
		writeFile(contentStringStream, new File(OFFLINE_PACKAGE_DIR_PATH + "plugins/" +
							CgrailsConstants.CGRAILS + "-" + pluginVersion + "/"
							 + JAVSCRIPT_DIR_NAME + PRELOADED_TEMPLATES_JS_PATH) );
	}
	
	/**
	 * Utility method to get Http response as a string.
	 * @param response HttpResponse object.
	 * @param requestUrl The URL of request.
	 * @return HttpResponse as String.
	 */
	private getResponseAsString(HttpResponse response, String requestUrl) {
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "  + response.getStatusLine().getStatusCode() + "for path:${requestUrl}");
		}
		BufferedReader br = new BufferedReader(
							 new InputStreamReader((response.getEntity().getContent())));

		StringBuffer htmlContent = new StringBuffer();
		String output = new String();
		while ((output = br.readLine()) != null) {
			htmlContent.append(output);
			htmlContent.append("\r\n");
		}
		return htmlContent.toString()
	}
	
	
	public void deleteOldPackage() {
		File sourceFile = new File(OFFLINE_PACKAGE_DIR_PATH)
		sourceFile.deleteDir();
	}
	
	void copyDirectory(File sourceFile, File targetFile) {
		if (sourceFile.isDirectory()) {
			if (!sourceFile.getName().contains(".svn")) {
				if (!targetFile.exists()) {
					targetFile.mkdirs();
				}
				String[] files = sourceFile.list();
				for (int i = 0; i < files.length; i++) {
					copyDirectory(new File(sourceFile, files[i]), new File(targetFile, files[i]));
				}
			}
			
		} else {
			if(!sourceFile.exists()){
				throw new RuntimeException("File or directory does not exist.");
			} else {
				if (!targetFile.getParentFile().exists()) {
					targetFile.getParentFile().mkdirs();
				}
				writeFile(new FileInputStream(sourceFile), targetFile);
			}
		}
	}
	
	private void writeFile(InputStream inputStream, File targetFile){
		targetFile.getParentFile().mkdirs();
		OutputStream outputStream = new FileOutputStream(targetFile);
		byte[] buf = new byte[1024];
		int len;
		while ((len = inputStream.read(buf)) > 0) {
			outputStream.write(buf, 0, len);
		}
		inputStream.close();
		outputStream.close();
	}
}
