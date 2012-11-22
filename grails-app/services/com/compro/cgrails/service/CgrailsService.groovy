package com.compro.cgrails.service

import grails.util.GrailsUtil

class CgrailsService {

	def static cgrailsConfig;
	
	def public getCgrailsConfiguration() {	
		if (cgrailsConfig == null) {
			def classLoader = Thread.currentThread().contextClassLoader
			cgrailsConfig = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('CgrailsConfig'));
		}
		return cgrailsConfig		
	}	
}
