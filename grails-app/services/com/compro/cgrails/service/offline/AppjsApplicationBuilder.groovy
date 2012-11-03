package  com.compro.cgrails.service.offline

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream




class AppjsApplicationBuilder {
	
	def OfflineApplicationBuilder
	
	def grailsApplication
	
	private static final String APP_JS_DIR_PATH = "appjs/"
	private static final String APP_JS_CONTENT_DIR = "content"
	private static final String APP_JS_WINDOW_DIR = "windows"
	private static final String APP_JS_MAC_DIR = "mac_os"
	private static final String MAC_OS_ZIP_NAME = "-macosx-offline.zip"
	private static final String WINDOW_ZIP_NAME = "-win32-offline.zip"
	private static final int BYTE_BUFFER_SIZE_1024 = 1024;
	private static final boolean INGNORE_ROOT_FOLDER = true;
	
	public void generateWindowsAppjs(String pluginDir,String pluginVersion) {
		File offlineWindowSource = new File(pluginDir + "/" + OfflineApplicationBuilder.OFFLINE_APP_DIR_PATH + APP_JS_DIR_PATH + APP_JS_WINDOW_DIR)
		File appjsWindowTargetDir = new File(OfflineApplicationBuilder.TARGET_OFFLINE_DIR_PATH + APP_JS_DIR_PATH + APP_JS_WINDOW_DIR);
		
		//copy appjs/windows resources to target/offine/appjs/windows folder
		OfflineApplicationBuilder.copyDirectory(offlineWindowSource, appjsWindowTargetDir);
		
		File offlineCoreSource = new File(OfflineApplicationBuilder.OFFLINE_PACKAGE_DIR_PATH);
		File appjsWindowContentDir = new File(appjsWindowTargetDir.getPath() + "/data/" + APP_JS_CONTENT_DIR);
		def applicationName = grailsApplication.metadata['app.name']
		def zipDir = applicationName + WINDOW_ZIP_NAME
		//copy offline package to windows app js content dir
		OfflineApplicationBuilder.copyDirectory(offlineCoreSource, appjsWindowContentDir);
		zipFolder(appjsWindowTargetDir.getPath(), OfflineApplicationBuilder.TARGET_OFFLINE_DIR_PATH + APP_JS_DIR_PATH + zipDir)
	}
	
	public void generateMacAppjs(String pluginDir,String pluginVersion) {
		File offlineMacSource = new File(pluginDir + "/" + OfflineApplicationBuilder.OFFLINE_APP_DIR_PATH + APP_JS_DIR_PATH + APP_JS_MAC_DIR)
		File appjsMacTargetDir = new File(OfflineApplicationBuilder.TARGET_OFFLINE_DIR_PATH + APP_JS_DIR_PATH + APP_JS_MAC_DIR);
		
		//copy appjs/mac resources to target/offine/appjs/mac folder
		OfflineApplicationBuilder.copyDirectory(offlineMacSource, appjsMacTargetDir);
		
		File offlineCoreSource = new File(OfflineApplicationBuilder.OFFLINE_PACKAGE_DIR_PATH);
		File appjsMacContentDir = new File(appjsMacTargetDir.getPath() + "/data/" + APP_JS_CONTENT_DIR);
		def applicationName = grailsApplication.metadata['app.name']
		def zipDir = applicationName + MAC_OS_ZIP_NAME
		//copy offline package to mac app js content dir
		OfflineApplicationBuilder.copyDirectory(offlineCoreSource, appjsMacContentDir);
		zipFolder(appjsMacTargetDir.getPath(),OfflineApplicationBuilder.TARGET_OFFLINE_DIR_PATH + APP_JS_DIR_PATH + zipDir)
	}
	
	
	
	public void deleteOldPackage() {
		File sourceFile = new File(OfflineApplicationBuilder.TARGET_OFFLINE_DIR_PATH + APP_JS_DIR_PATH)
		sourceFile.deleteDir();
	}
	
	

	/** Function to compress a folder.
	 * @param sourceFolder folder to be compressed
	 * @param zipFile destination zip file.
	 * @throws IOException
	 */
	public void zipFolder(final String sourceFolder, final String zipFile) throws IOException {

		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		//File writer
		fileWriter = new FileOutputStream(zipFile);
		//Creating Zip file.
		zip = new ZipOutputStream(fileWriter);
		//Adding folders to zip file.
		addFolderToZip("", sourceFolder, zip);
		zip.flush();
		zip.close();
	}
	
   
   
	/** Function to add a File to Zip.
	 *
	 * @param path file path
	 * @param srcFile file folder
	 * @param zip zip output stream
	 * @throws IOException
	 */
	private void addFileToZip(final String path, final String srcFile,
			final ZipOutputStream zip) throws IOException {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[BYTE_BUFFER_SIZE_1024];
			int len;
			FileInputStream input = new FileInputStream(srcFile);
			if (INGNORE_ROOT_FOLDER) {
				String temp = "";
				if (path.contains("/")) {
					temp = path.substring(path.indexOf("/") + 1, path.length());
				}
				if (temp.equals("")) {
					zip.putNextEntry(new ZipEntry(folder.getName()));
				} else {
					zip.putNextEntry(new ZipEntry(temp + "/" + folder.getName()));
				}
			} else {
				zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			}
			while ((len = input.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	/** Function to add a folder to Zip.
	 *
	 * @param path file path
	 * @param srcFolder file folder
	 * @param zip zip output stream
	 * @throws IOException
	 */
	private void addFolderToZip(final String path, final String srcFolder,
			final ZipOutputStream zip) throws IOException {
		File folder = new File(srcFolder);
		//Iterate over files in folder
		for (String fileName : folder.list()) {
			//add file in the folder to zip file
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
	}
	
  


}
