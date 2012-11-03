package com.compro.cgrails.service.engine

import grails.util.GrailsUtil

import org.springframework.web.context.request.RequestContextHolder

import com.compro.cgrails.service.CgrailsService


public class DefaultCgrailsEngineImpl implements CgrailsEngine {
	
	def grailsApplication
	
	CgrailsService cgrailsService
	
	public String getSkin() {
		HashMap<?,?> paramsMap = RequestContextHolder.currentRequestAttributes().params
		String skin = paramsMap["skin"]
		def classLoader = Thread.currentThread().contextClassLoader
		def cgrailsConfig = cgrailsService.getCgrailsConfiguration();
		if(skin != null) {
			if ((!cgrailsConfig.cgrails.skinning.skins."${skin}") && 
				(skin != cgrailsConfig.cgrails.skinning.baseskin)){
				return cgrailsConfig.cgrails.skinning.defaultskin
			} else {
				return skin
			}
		} else {
			return cgrailsConfig.cgrails.skinning.defaultskin
		}
	}	
}
