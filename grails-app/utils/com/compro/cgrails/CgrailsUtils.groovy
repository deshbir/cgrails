package com.compro.cgrails

import java.util.List;

import javax.servlet.http.HttpServletRequest

import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.context.ApplicationContext
import org.springframework.web.context.request.RequestContextHolder

class CgrailsUtils {	
	public static final String RIGHT_TO_LEFT = "rtl"
	public static final String LEFT_TO_RIGHT = "ltr"
	public static final List<String> RTL_LANGUAGES = ["iw","ar","fa","ur"]
	
	def static String getSkin() {
		def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
		def cgrailsService = applicationContext.getBean("cgrailsService");
		return cgrailsService.getSkin();
	}
	
	def static String getWorkflow() {
		def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
		def cgrailsService = applicationContext.getBean("cgrailsService");
		return cgrailsService.getWorkflow();
	}
	
	def static getOrientation(String lang) {
		if (RTL_LANGUAGES.contains(lang)) {
			RIGHT_TO_LEFT;
		} else {
			LEFT_TO_RIGHT;
		}
	}
	
}
