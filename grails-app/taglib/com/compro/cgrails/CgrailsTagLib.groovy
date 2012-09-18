package com.compro.cgrails
import org.codehaus.groovy.grails.commons.GrailsApplication

class CgrailsTagLib {
	
	static namespace = "cgrails"

	GrailsApplication grailsApplication	
	
	def stylesheet = { attrs, body ->
		String src = attrs.remove('src')
		if (!src) {
			throwTagError("Tag [less] is missing required attribute [src]")
		}
		
		String fileType	
		String currentSkin = CgrailsUtils.getSkin()
		String dir = "${CgrailsConstants.CGRAILS_CSS_PATH}/${currentSkin}"		
		if (isDebugMode() && (CgrailsUtils.getWorkflow() != CgrailsConstants.WORKFLOW_OFFLINE)) {
			// reference .less files directly (In browser, less.js will compile into CSS)
			fileType = '.less'
			dir = "${dir}/${CgrailsConstants.LESS_FOLDER_NAME}"				
		} else {
			fileType = '.css'
		}		
		
		String filePath = "${dir}/${src}${fileType}"
		def resource =  grailsAttributes.getPagesTemplateEngine().getResourceForUri(filePath)
		//Fallback to parent skin , if less/css not found in current skin
		while (!resource.exists() && (currentSkin != grailsApplication.config.cgrails.skinning.baseskin)) {
			def parentSkin = grailsApplication.config.cgrails.skinning.skins."${currentSkin}".parent
			filePath = filePath.replaceFirst(currentSkin, parentSkin)
			resource = grailsAttributes.getPagesTemplateEngine().getResourceForUri(filePath)
			currentSkin = parentSkin
		}
		if (isDebugMode() && (CgrailsUtils.getWorkflow() != CgrailsConstants.WORKFLOW_OFFLINE)) {
			String appName = grailsApplication.metadata['app.name']
			// reference .less files directly (In browser, less.js will compile into CSS)
			Long timestamp = System.currentTimeMillis()
			out <<  "<link type='text/css' rel='stylesheet/less' href='/${appName}/${filePath}?_debugResources=y&n=$timestamp'/>"
			out <<  r.external(uri : "${pluginContextPath }/${CgrailsConstants.LESS_SCRIPT_FILE_LOCATION}")
			if (isAutoReloadLessChanges(attrs)) {
				out << "<script type='text/javascript'>less.env = 'development';less.watch();</script>"
			}
		} else {
			out << r.external(uri : "/" + filePath)
		}	
	}
	
	def stylesheet_rtl = { attrs, body ->
		String src = attrs.remove('src')
		if (!src) {
			throwTagError("Tag [less] is missing required attribute [src]")
		}
		src = src + "-rtl"		
		String fileType = '.css'
				
		String currentSkin = CgrailsUtils.getSkin()
		String dir = "${CgrailsConstants.CGRAILS_CSS_PATH}/${currentSkin}"
		String filePath = "${dir}/${src}${fileType}"
		def resource =  grailsAttributes.getPagesTemplateEngine().getResourceForUri(filePath)
		while (!resource.exists() && (currentSkin != grailsApplication.config.cgrails.skinning.baseskin)) {
			def parentSkin = grailsApplication.config.cgrails.skinning.skins."${currentSkin}".parent
			filePath = filePath.replaceFirst(currentSkin, parentSkin)
			resource = grailsAttributes.getPagesTemplateEngine().getResourceForUri(filePath)
			currentSkin = parentSkin
		}
		out << r.external(uri : "/" + filePath)
	}

//	def scripts = { attrs, body ->
//		if (isDebugMode() && (CgrailsUtils.getWorkflow() != CgrailsConstants.WORKFLOW_OFFLINE)) {
//			
//			out <<  r.external(uri : "${pluginContextPath }/${CgrailsConstants.LESS_SCRIPT_FILE_LOCATION}")
//
//			if (isUsingAutoReload(attrs)) {
//				out << "<script type='text/javascript'>less.env = 'development';less.watch();</script>"
//			}
//		}
//	}

	private boolean isDebugMode() {
		if(grailsApplication.config.grails.resources.debug){
			return true
		} else {
			return false
		}
	}
	
	private boolean isAutoReloadLessChanges(attrs) {
		def watch = attrs.watch
		return !(watch == null || watch == "false")
	}
}


