package com.compro.cgrails.service

import javax.servlet.http.HttpServletRequest;

import grails.util.GrailsUtil

import org.codehaus.groovy.grails.web.sitemesh.GroovyPageLayoutFinder;
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class SkinningService {

    def groovyPagesTemplateEngine
	GroovyPageLayoutFinder groovyPageLayoutFinder
	def grailsApplication
	
	public String getCalculatedSkinForResource (String filePath, String currentSkin){
		def classLoader = Thread.currentThread().contextClassLoader
		def cgrailsConfig =  new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('CgrailsConfig'))
		def resource =  groovyPagesTemplateEngine.getResourceForUri(filePath)
		//falls back to parent
		while (!resource.exists() && (currentSkin != cgrailsConfig.cgrails.skinning.baseskin)) {
			def parentSkin = cgrailsConfig.cgrails.skinning.skins."${currentSkin}".parent
			filePath = filePath.replaceFirst(currentSkin, parentSkin)
			resource = groovyPagesTemplateEngine.getResourceForUri(filePath)
			currentSkin = parentSkin
		}
		return currentSkin
	}
	
	public def getSkinnedDecorator (HttpServletRequest request, String currentSkin, def fulllayoutPath){
		def decorator = groovyPageLayoutFinder.getNamedDecorator(request,fulllayoutPath)
		def classLoader = Thread.currentThread().contextClassLoader
		def cgrailsConfig = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('CgrailsConfig'))
		//falls back to parent
		while (decorator == null && (currentSkin != cgrailsConfig.cgrails.skinning.baseskin)) {
			def parentSkin = cgrailsConfig.cgrails.skinning.skins."${currentSkin}".parent
			fulllayoutPath = fulllayoutPath.replaceFirst(currentSkin, parentSkin)
			decorator = groovyPageLayoutFinder.getNamedDecorator(request,fulllayoutPath)
			currentSkin = parentSkin
		}
		return decorator
	}
}
