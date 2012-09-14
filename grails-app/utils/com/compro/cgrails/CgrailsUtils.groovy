package com.compro.cgrails

import javax.servlet.http.HttpServletRequest

import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.context.ApplicationContext
import org.springframework.web.context.request.RequestContextHolder

class CgrailsUtils {	
	
	static String getSkin() {
		def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
		def cgrailsService = applicationContext.getBean("cgrailsService");
		return cgrailsService.getSkin();
	}
	
	static String getWorkflow() {
		def applicationContext = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
		def cgrailsService = applicationContext.getBean("cgrailsService");
		return cgrailsService.getWorkflow();
	}
	
}
