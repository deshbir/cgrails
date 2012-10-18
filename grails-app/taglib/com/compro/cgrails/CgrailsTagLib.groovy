package com.compro.cgrails
import org.codehaus.groovy.grails.commons.GrailsApplication

import com.compro.cgrails.service.SkinningService

class CgrailsTagLib {
	
	static namespace = "cgrails"
	GrailsApplication grailsApplication
	SkinningService skinningService
	
	def stylesheet = { attrs, body ->
		if(attrs.rtlsupport.equals('false')){
			getLTRStyleSheet(attrs)
		} else {
			def direction = CgrailsUtils.getOrientation()
			if(direction == CgrailsUtils.LEFT_TO_RIGHT){
				getLTRStyleSheet(attrs)
			} else {
				getRTLStyleSheet(attrs)
			}
		}
	}
	
	/**
	 * This tag sets up environment for Cgrails.
	 * The tag actually inlcudes "views/_setupCgrailsEnvironment.gsp" template inside the GSP.
	 * This template contains the required code to setup Cgrails environment. 
	 */
	def environment_setup = { attrs, body ->
		// renders the setupCgrailsEnvironment template
		out << g.render(template : "/setupCgrailsEnvironment", plugin : "cgrails")		
	}
	
	def switch_singlepage = { attrs, body ->
		String actionName = attrs.action
		String appName = grailsApplication.metadata['app.name']
		String href
		if (CgrailsUtils.getWorkflow() == CgrailsConstants.WORKFLOW_OFFLINE) {
			href = actionName + ".html"
		} else {		
			href = "../singlepage/" + actionName
		}
		out << href
	}
	
	private void getLTRStyleSheet(def attrs){
		String fileType
		String currentSkin = CgrailsUtils.getSkin()
		String src = attrs.remove('src')
		if (!src) {
			throwTagError("Tag [less] is missing required attribute [src]")
		}
		String dir = "${CgrailsConstants.CGRAILS_CSS_PATH}/${currentSkin}"
		if (isDebugMode() && (CgrailsUtils.getWorkflow() != CgrailsConstants.WORKFLOW_OFFLINE)) {
			// reference .less files directly (In browser, less.js will compile into CSS)
			fileType = '.less'
			dir = "${dir}/${CgrailsConstants.LESS_FOLDER_NAME}"
		} else {
			fileType = '.css'
		}
		String filePath = "${dir}/${src}${fileType}"
		def fallbackSkin = skinningService.getCalculatedSkinForResource(filePath,currentSkin)
		filePath = filePath.replaceFirst(currentSkin, fallbackSkin)
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
		return
	}
	private void getRTLStyleSheet(def attrs){
		String src = attrs.remove('src')
		if (!src) {
			throwTagError("Tag [less] is missing required attribute [src]")
		}
		src += "-rtl"
		String fileType = '.css'
		if (!src) {
			throwTagError("Tag [less] is missing required attribute [src]")
		}
		String currentSkin = CgrailsUtils.getSkin()
		String dir = "${CgrailsConstants.CGRAILS_CSS_PATH}/${currentSkin}"
		String filePath = "${dir}/${src}${fileType}"
		def fallbackSkin = skinningService.getCalculatedSkinForResource(filePath,currentSkin)
		filePath = filePath.replaceFirst(currentSkin, fallbackSkin)
		out << r.external(uri : "/" + filePath)
		return
	}
	
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


