package com.compro.cgrails

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.web.context.request.RequestContextHolder

class CgrailsUtils {	
	public static final String RIGHT_TO_LEFT = "rtl"
	public static final String LEFT_TO_RIGHT = "ltr"
	public static final List<String> RTL_LANGUAGES = ["iw","ar","fa","ur"]
	
	def static String getSkin() {
		def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
		def cgrailsEngine = applicationContext.getBean("cgrailsEngine");
		return cgrailsEngine.getSkin();
	}
	
	def static String getWorkflow() {
		HashMap<?,?> paramsMap = RequestContextHolder.currentRequestAttributes().params
		String workflow = paramsMap["workflow"]	
		if (workflow == null) {
			return CgrailsConstants.WORKFLOW_TRADITIONAL
		}	
		return workflow
	}
	
	def static getOrientation() {
		//getting the orientation set in the session
		def session = RequestContextHolder.currentRequestAttributes().getSession()
		String locale  = session.'org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE'
		if(locale){
			if (RTL_LANGUAGES.contains(locale)) {
				RIGHT_TO_LEFT;
			} else {
				LEFT_TO_RIGHT;
			}
		} else
			LEFT_TO_RIGHT
	}
	
}
