package com.compro.cgrails.service

import grails.util.GrailsUtil

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class SkinningService {

    def groovyPagesTemplateEngine
	
	public String getResourceFallbackSkin(String filePath, String currentSkin){
		def classLoader = Thread.currentThread().contextClassLoader
		def cgrailsConfig
		def baseSkin
		try{
			cgrailsConfig = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('CgrailsConfig'))
		}catch (ClassNotFoundException e) {
			System.out.println("Cgrails configuration file not found. Please add CgrailsConfig.groovy file.");
		}	
		def resource =  groovyPagesTemplateEngine.getResourceForUri(filePath)
		while (!resource.exists() && (currentSkin != cgrailsConfig.cgrails.skinning.baseskin)) {
			def parentSkin = cgrailsConfig.cgrails.skinning.skins."${currentSkin}".parent
			filePath = filePath.replaceFirst(currentSkin, parentSkin)
			resource = groovyPagesTemplateEngine.getResourceForUri(filePath)
			currentSkin = parentSkin
		}
		return currentSkin
	}
}
