package com.compro.cgrails.service

import grails.util.GrailsUtil

import org.springframework.web.context.request.RequestContextHolder


public class DefaultCgrailsServiceImpl implements CgrailsService {
	
	def grailsApplication
	
	public String getSkin() {
		HashMap<?,?> paramsMap = RequestContextHolder.currentRequestAttributes().params
		String skin = paramsMap["skin"]
		def classLoader = Thread.currentThread().contextClassLoader
		def cgrailsConfig = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('CgrailsConfig'))
		if(skin != null) {
			if ((!cgrailsConfig.cgrails.skinning.skins."${skin}")
				|| (!cgrailsConfig.cgrails.skinning.skins."${skin}".parent)){
				return cgrailsConfig.cgrails.skinning.baseskin
			} else {
				return skin
			}
		} else {
			return cgrailsConfig.cgrails.skinning.baseskin
		}
	}
	
	public String getWorkflow() {
		HashMap<?,?> paramsMap = RequestContextHolder.currentRequestAttributes().params
		String workflow = paramsMap["workflow"]
		return workflow
	}
	
}
