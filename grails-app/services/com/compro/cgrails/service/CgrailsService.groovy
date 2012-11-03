package com.compro.cgrails.service

import grails.util.GrailsUtil

class CgrailsService {

	def static cgrailsConfig;
	
	def public getCgrailsConfiguration() {	
		if (cgrailsConfig == null) {
			cgrailsConfig = new ConfigSlurper(GrailsUtil.environment).parse(new File("grails-app/conf/CgrailsConfig.groovy").toURI().toURL());
		}
		return cgrailsConfig		
	}
	
}
