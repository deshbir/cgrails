package com.compro.cgrails.service

import javax.servlet.http.HttpServletRequest;


import grails.util.GrailsUtil

import org.codehaus.groovy.grails.web.sitemesh.GroovyPageLayoutFinder;
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware


/**
 * Provides service level functions related to Cgrails Skinning and Fallback functionality.
 */
class SkinningService {

    def groovyPagesTemplateEngine
	
	GroovyPageLayoutFinder groovyPageLayoutFinder
	
	def grailsApplication
	
	CgrailsService cgrailsService
	
	public String getCalculatedSkinForResource (String resourcePath, String currentSkin){		
		def classLoader = Thread.currentThread().contextClassLoader		
		//Reading Cgrails Configuration
		def cgrailsConfig =  cgrailsService.getCgrailsConfiguration();
		//Getting resource from resource path		
		def resource =  groovyPagesTemplateEngine.getResourceForUri(resourcePath)
		/**
		 * FALLBACK LOOP
		 * If the requested resource is found in current skin, exit the loop.
		 * But if the requested resource is not found in the current skin,  
		 *         fallback to same resource (same physical path) in the parent skin of current skin. 
		 *         If the resource is found in parent skin, assign parent skin as calculated skin and exit the loop.
		 * But If the resource is not found in parent skin,  
		 *         fallback to grandparent and continue the process till application default skin is reached.
		 */
		while (!resource.exists() && (currentSkin != cgrailsConfig.cgrails.skinning.baseskin)) {
			//Read the parent skin from Cgrails Configuration.
			def parentSkin = cgrailsConfig.cgrails.skinning.skins."${currentSkin}".parent
			// Replace skin with parent skin in resource path.
			resourcePath = resourcePath.replaceFirst(currentSkin, parentSkin)
			//get the parent skin resource
			resource = groovyPagesTemplateEngine.getResourceForUri(resourcePath)
			// assign parent skin as current skin
			currentSkin = parentSkin
		}
		// return the calculated skin after applying Skinning and Fallback logic.
		return currentSkin
	}
	
	public def getSkinnedDecorator (HttpServletRequest request, String currentSkin, def fulllayoutPath){
		//Getting decorator from layout path
		def decorator = groovyPageLayoutFinder.getNamedDecorator(request,fulllayoutPath)
		def classLoader = Thread.currentThread().contextClassLoader
		//Reading Cgrails Configuration
		def cgrailsConfig = cgrailsService.getCgrailsConfiguration();
		/**
		 * FALLBACK LOOP
		 * If the requested layout/decorator is found in current skin, exit the loop.
		 * But if the requested layout/decorator is not found in the current skin,  
		 *         fall back to same layout/decorator (same physical path) in the parent skin of current skin. 
		 *         If the layout/decorator is found in parent skin, exit the loop.
		 * But If the layout/decorator is not found in parent skin,  
		 *         fall back to grandparent and continue the process till application default skin is reached.
		 */
		while (decorator == null && (currentSkin != cgrailsConfig.cgrails.skinning.baseskin)) {
			//Read the parent skin from Cgrails Configuration.
			def parentSkin = cgrailsConfig.cgrails.skinning.skins."${currentSkin}".parent
			// Replace skin with parent skin in layout path.
			fulllayoutPath = fulllayoutPath.replaceFirst(currentSkin, parentSkin)
			decorator = groovyPageLayoutFinder.getNamedDecorator(request,fulllayoutPath)			
			currentSkin = parentSkin
		}
		// return the calculated decorator after applying Skinning and Fallback logic.
		return decorator
	}
}
