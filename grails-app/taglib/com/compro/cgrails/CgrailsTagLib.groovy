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
		String href
		if (CgrailsUtils.getWorkflow() == CgrailsConstants.WORKFLOW_OFFLINE) {
			href = actionName + ".html"
		} else {	
			String ctxPath = request.contextPath
			String currentSkin = CgrailsUtils.getSkin()
			href = "${ctxPath}/${currentSkin}/singlepage/" + actionName
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
		if (isDebugMode() && (CgrailsUtils.getWorkflow() != CgrailsConstants.WORKFLOW_OFFLINE)) {
			// reference .less files directly (In browser, less.js will compile into CSS)
			fileType = '.less'
		} else {
			fileType = '.css'
		}
		String filePath = "${CgrailsConstants.CGRAILS_CSS_PATH}/${currentSkin}/${CgrailsConstants.LESS_FOLDER_NAME}/${src}${fileType}"
		def fallbackSkin = skinningService.getCalculatedSkinForResource(filePath,currentSkin)
		filePath = filePath.replaceFirst(currentSkin, fallbackSkin)
		if (isDebugMode() && (CgrailsUtils.getWorkflow() != CgrailsConstants.WORKFLOW_OFFLINE)) {
			String ctxPath = request.contextPath
			// reference .less files directly (In browser, less.js will compile into CSS)
			Long timestamp = System.currentTimeMillis()
			out <<  "<link type='text/css' rel='stylesheet/less' href='${ctxPath}/${filePath}?_debugResources=y&n=$timestamp'/>"
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
		if (!src) {
			throwTagError("Tag [less] is missing required attribute [src]")
		}
		String currentSkin = CgrailsUtils.getSkin()
		String filePath = "${CgrailsConstants.CGRAILS_CSS_PATH}/${currentSkin}/${CgrailsConstants.LESS_FOLDER_NAME}/${src}.css"
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


