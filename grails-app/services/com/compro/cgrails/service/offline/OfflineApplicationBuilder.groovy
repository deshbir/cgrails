package  com.compro.cgrails.service.offline


import net.sf.json.JSONObject

import org.apache.commons.io.IOUtils
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.CookieStore
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.protocol.ClientContext
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.protocol.HttpContext
import org.apache.http.util.EntityUtils

import com.compro.cgrails.CgrailsConstants
import com.compro.cgrails.exception.UnauthorizedUserException
import com.compro.cgrails.service.CgrailsService

class OfflineApplicationBuilder {
	
	static final String TARGET_OFFLINE_DIR_PATH = "target/offline/"
	private static final String OFFLINE_CORE_DIR_PATH = "core/"
	static final String OFFLINE_PACKAGE_DIR_PATH = TARGET_OFFLINE_DIR_PATH + OFFLINE_CORE_DIR_PATH	
	static final String OFFLINE_APP_DIR_PATH = "web-app/offline-app/"	
	private static final String WEBAPP_DIR_NAME = "web-app"	
	private static final String PAGES_DIR_PATH = "grails-app/views/pages/"	
	private static final String JAVSCRIPT_DIR_NAME = "js"
	private static final String IMAGES_DIR_NAME = "images"
	private static final String FONTS_DIR_NAME = "fonts"
	private static final String CSS_DIR_NAME = "css"
	private static final String INDEX_FILE_NAME = "index.html"	
	private static final String TEMPLATES_FOLDER_NAME = "templates"	
	private static final String PRELOADED_TEMPLATES_JS_PATH = "/offline/core/preloaded_templates.js"
	private static final String PRELOADED_MODELS_JS_PATH = "/offline/core/preloaded_model.js"	
	private static final String APP_HOST = "localhost"
	private static final String APP_PORT = "8080"	
	private CookieStore cookieStore = new BasicCookieStore();
	private HttpContext httpContext = new BasicHttpContext();
	private HttpClient httpClient = new DefaultHttpClient();
	/***************************************
	 * AutoWiring grailsApplication instance 
	 ***************************************/ 
	def grailsApplication
	
	CgrailsService cgrailsService
	
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

	private static FileFilter cssFileFilter = new FileFilter() {
		public boolean accept(File file) {
			return file.getName().contains(".css");
		}
	}
	
	/***********************************************************************
	 * Function to delete older Offline package (if any) in the application.
	 ***********************************************************************/
	public void deleteOldPackage() {
		File sourceFile = new File(OFFLINE_PACKAGE_DIR_PATH)
		sourceFile.deleteDir();
	}
	
	/***********************************************************************
	 * Function to copy required JavaScript files in the Offline package.
	 ***********************************************************************/
	public void copyScripts(String pluginDir, String pluginVersion) {		
		/******************************************************************************
		 * Copying required JavaScript files from cgrails plugin in the Offline package
		 ******************************************************************************/
		File offlineJSsource = new File(pluginDir + "/" + WEBAPP_DIR_NAME + "/" +JAVSCRIPT_DIR_NAME + "/offline/core");			
		File targetOfflineJSfolder = new File(OFFLINE_PACKAGE_DIR_PATH + "plugins/" +
			CgrailsConstants.CGRAILS + "-" + pluginVersion + "/" + JAVSCRIPT_DIR_NAME + "/offline/core");
		copyDirectory(offlineJSsource, targetOfflineJSfolder);		
		
		File cgrailsLibsJSsource = new File(pluginDir + "/" + WEBAPP_DIR_NAME + "/" + JAVSCRIPT_DIR_NAME + "/libs");
		File targetLibsJSfolder = new File(OFFLINE_PACKAGE_DIR_PATH + "plugins/" +
			CgrailsConstants.CGRAILS + "-" + pluginVersion + "/" + JAVSCRIPT_DIR_NAME + "/libs");
		copyDirectory(cgrailsLibsJSsource, targetLibsJSfolder);
		
		/******************************************************************************
		 * Copying required JavaScript files from application in the Offline package
		 ******************************************************************************/
		File src = new File(WEBAPP_DIR_NAME + "/" + JAVSCRIPT_DIR_NAME);
		File dst = new File(OFFLINE_PACKAGE_DIR_PATH + JAVSCRIPT_DIR_NAME);
		copyDirectory(src, dst);
	}
	
	/***********************************************************************
	 * Function to copy required image files in the Offline package.
	 ***********************************************************************/
	public void copyImages() {
		File src = new File(WEBAPP_DIR_NAME + "/" + IMAGES_DIR_NAME);
		File dst = new File(OFFLINE_PACKAGE_DIR_PATH + IMAGES_DIR_NAME);
		copyDirectory(src, dst);
	}
	
	/***********************************************************************
	 * Function to copy required CSS files in the Offline package.
	 ***********************************************************************/
	public void copyStyles(String skin) {
		/*************************************************************
		 * STEP 1: Copy everything inside css folder except cgrails folder.
		 *************************************************************/
		File src = new File(WEBAPP_DIR_NAME + "/" + CSS_DIR_NAME);
		File[] files = src.listFiles(cgrailsFileFilter);
		for(File file :  files){
			String filePath = file.getPath();
			filePath = filePath.replace(WEBAPP_DIR_NAME, OFFLINE_PACKAGE_DIR_PATH);
			File destFile = new File(filePath);
			copyDirectory(file, destFile);
		}
		/*****************************************
		 * STEP 2: Copy all CSS files of a skin.
		 ***************************************/
		File skinLessDir = new File(WEBAPP_DIR_NAME + "/" + CSS_DIR_NAME + "/"
										+ CgrailsConstants.CGRAILS + "/" + skin + "/less");
		File[] cssFileSrc = skinLessDir.listFiles(cssFileFilter);
		for(File file :  cssFileSrc){
			String filePath = file.getPath();
			filePath = filePath.replace(WEBAPP_DIR_NAME, OFFLINE_PACKAGE_DIR_PATH);
			File cssDestFile = new File(filePath);
			copyDirectory(file, cssDestFile);
		}
	}
	
	/***********************************************************************
	 * Function to create HTML(s) for all the singlepage modules in the offline package.
	 ***********************************************************************/
	public void createSinglepageHtmls(String skin, String locale, String mode) {
		//Reading "SinglepageController" inside application	
		def singlepageController = grailsApplication.getArtefact("Controller", "com.compro.cgrails.SinglepageController")
		
		//Get all the actions of "SinglepageController"
		def actions = new HashSet<String>()
		for (String uri : singlepageController.uris ) {
			actions.add(singlepageController.getMethodActionName(uri))
		}
		
		//Create HTML for every action. The name of HTML is same as action name.
		Iterator actionsIter = actions.iterator();		
		while(actionsIter.hasNext()) {
		  String actionName = actionsIter.next();
		  writeSinglePage(actionName, actionName + ".html", skin, locale, mode);
		}
	}

	
	/***********************************************************************
	 * Function to request SinglePage HTML and write response as HTML to disk.
	 ***********************************************************************/
	private void writeSinglePage(String pageName, String targetFileName, String skin, String locale, String mode) {
		def urlBuilder = new StringBuilder("http://");
		urlBuilder.append(APP_HOST).append(":").append(APP_PORT).append("/").append(grailsApplication.metadata['app.name']);
		urlBuilder.append("/").append(skin).append("/singlepage/").append(pageName)
		.append("?workflow=").append(CgrailsConstants.WORKFLOW_OFFLINE)
		.append("&lang=").append(locale);
		if(mode == "debugAir"){
			urlBuilder.append('&mode=debugAir');
		}
		//Request SinglePage Module
		HttpResponse response = makeHttpGet(urlBuilder.toString())
		//Validate HTML response
		validateResponse(response, urlBuilder.toString());
		//Write response as HTML to disk
		writeFile(response.getEntity().getContent(), new File(OFFLINE_PACKAGE_DIR_PATH + targetFileName));
	}
	
	/***********************************************************************
	 * Function to pre-load templates in Offline package.
	 * Create a JavaScript file which contains all the templates as JSON.
	 ***********************************************************************/
	public void preloadTemplates(String skin, String locale, String pluginVersion) {
		def classLoader = Thread.currentThread().contextClassLoader
		def config = cgrailsService.getCgrailsConfiguration();
		Boolean isConfigurable = config.cgrails.templates.useConfiguration
		Set<String> templateList
		
		/************************************************************************
		 * If configurable, Get the list of templates from configuration file.
		 * Else, get the list of templates by fetching list from "templates" folder.
		 *************************************************************************/
		if(isConfigurable) {
			templateList = new HashSet(config.cgrails.templates.templateList);
		} else {
			templateList = getRequiredTemplates(skin, config);
		}
		
		JSONObject jsonTemplate = new JSONObject();
		String template;
		for (String templatename :  templateList) {
			def urlBuilder = new StringBuilder("http://");
			urlBuilder.append(APP_HOST).append(":").append(APP_PORT).append("/")
				.append(grailsApplication.metadata['app.name']).append("/cgrailstemplate/?workflow=")
				.append(CgrailsConstants.WORKFLOW_OFFLINE)
				.append("&lang=").append(locale);
			Map<String,String> parameters = new HashMap<String,String>(2);
			parameters.put("skin", skin)
			parameters.put("path", TEMPLATES_FOLDER_NAME + "/" + templatename);
			//Request a template
			HttpResponse response = makeHttpPost(urlBuilder.toString(), parameters)
			//Get template response as String
			template = getResponseAsString(response, urlBuilder.toString(), true);
			template = template.replace("\r\n", "").replace("\t", "");
			jsonTemplate.put(templatename, template);
			HttpEntity entity = response.getEntity()
			entity.consumeContent()
		}
		String contentString = "com.compro.cgrails.OfflineTemplates = " + jsonTemplate.toString(8);
				
		//Write templates as JSON object to file.
		writeFile(contentString, new File(OFFLINE_PACKAGE_DIR_PATH + "plugins/" +
							CgrailsConstants.CGRAILS + "-" + pluginVersion + "/"
							 + JAVSCRIPT_DIR_NAME + PRELOADED_TEMPLATES_JS_PATH) );
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
	
	/***********************************************************************
	 * Function to pre-load models in Offline package.
	 * Create a JavaScript file which contains all the model data as JSON.
	 ***********************************************************************/
	private void preloadModel(String pluginVersion) {
		def classLoader = Thread.currentThread().contextClassLoader
		def config = cgrailsService.getCgrailsConfiguration();
		String javascriptmvc = config.cgrails.javascriptMVC
		if(javascriptmvc == null || javascriptmvc == "backbone") {
			createBacbonePreloadedModel(pluginVersion);
		}				
	}
	
	private void createBacbonePreloadedModel(String pluginVersion) {
		String appName = grailsApplication.metadata['app.name']		
		def preloadedModelBuffer = new StringBuffer();		
		preloadedModelBuffer.append("com.compro.cgrails.PreloadedModel = ");
		
		JSONObject modelsJSON = new JSONObject();
		
		//Get all domain classes of application.
		def domainClasses = grailsApplication.domainClasses
		for (def domainClass:domainClasses) {
			Class domain = domainClass.clazz
			String[] cachedUrls
			try {
				// call initialData function to get pre-loaded data.
				cachedUrls = domain.offlineCachedUrls()
			} catch (MissingMethodException e) {
				// If initialData function not found, do nothing. Continue to next item
				continue
			}	
			for (String url : cachedUrls) {
				def urlBuilder = new StringBuilder("http://");
				urlBuilder.append(APP_HOST).append(":").append(APP_PORT).append("/")
				.append(appName).append(url);
				String modifiedUrl = urlBuilder.toString();
				modifiedUrl += (modifiedUrl.indexOf('?') >= 0) ? "&workflow=" + CgrailsConstants.WORKFLOW_OFFLINE : "?workflow=" + CgrailsConstants.WORKFLOW_OFFLINE
				HttpResponse response = makeHttpGet(modifiedUrl)				
				String apiResponse = getResponseAsString(response, urlBuilder.toString(), false);
				modelsJSON.put("/" + appName + url, apiResponse);
			}
			
			
		}
		preloadedModelBuffer.append(modelsJSON.toString(8));
								
		File file = new File(OFFLINE_PACKAGE_DIR_PATH + "plugins/" + CgrailsConstants.CGRAILS
								+ "-" + pluginVersion + "/" + JAVSCRIPT_DIR_NAME + PRELOADED_MODELS_JS_PATH);
		
		//Append to existing file. Dont overwrite				
		FileWriter fileWriter = new FileWriter(file, true);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		bufferWriter.write(preloadedModelBuffer.toString());
		bufferWriter.close();
	}
	
	
	/***************************************
	 * UTILITY FUNCTIONS
	 ******************************************/
	
	/**
	 * Utility method to get Http response as a string.
	 * @param response HttpResponse object.
	 * @param requestUrl The URL of request.
	 * @return HttpResponse as String.
	 */
	private String getResponseAsString(HttpResponse response, String requestUrl, boolean addLineBreaks) {
		validateResponse(response, requestUrl);
		return IOUtils.toString(response.getEntity().getContent(), "UTF-8");		
	}
	
	private void validateResponse(HttpResponse response, String requestUrl) {
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "  + response.getStatusLine().getStatusCode() + "for path:${requestUrl}");
		}
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
				FileInputStream inputStream = new FileInputStream(sourceFile);
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
	}
	
	private void writeFile(InputStream inputStream, File targetFile){
		targetFile.getParentFile().mkdirs();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));		
			
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter (
														new FileOutputStream(targetFile), "UTF-8"));															
		int c = 1024;
		while ((c = reader.read()) != -1) {
			writer.write(c);
		}			
		reader.close();
		writer.close();
	}
	
	private void writeFile(String inputString, File targetFile){
		targetFile.getParentFile().mkdirs();
			
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter (
														new FileOutputStream(targetFile), "UTF-8"));
		writer.write(inputString);
		writer.close();
	}
	public void authenticate(){
			def config = cgrailsService.getCgrailsConfiguration();
			def username = config.cgrails.offline.username
			def password = config.cgrails.offline.password
			if(!(username && password)){
				throw new UnauthorizedUserException()
			}
			httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			def urlBuilder = new StringBuilder("http://localhost:8080/");
			urlBuilder.append(grailsApplication.metadata['app.name']).append("/j_spring_security_check");
			Map<String,String> parameters = new HashMap<String,String>(2);
			parameters.put("j_username", username)
			parameters.put("j_password", password);
			HttpResponse response = makeHttpPost(urlBuilder.toString(),parameters);
			HttpEntity entity = response.getEntity();			
			entity.consumeContent();
	}
	private HttpResponse makeHttpPost(String url,Map<String,String> parameters){
		HttpPost httpPost = new HttpPost(url);
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		Set<String> paramKeys =  parameters.keySet();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for(String param : paramKeys){
			nameValuePairs.add(new BasicNameValuePair(param, parameters.get(param)));
		}
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = httpClient.execute(httpPost, httpContext);
		return response;
	}
	private HttpResponse makeHttpGet(String url){
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = httpClient.execute(httpGet, httpContext);
		return response;
	}
	
}
