package com.compro.cgrails

import com.compro.cgrails.service.SkinningFallbackService

class OverrideGrailsTagLib {
	
	static namespace = "g"
	SkinningFallbackService skinningFallbackService
	
	def include = { attrs,body ->
		if(attrs.view) {
			if(!(attrs.controller && attrs.action)) {
				if (attrs.params == null) {
					attrs.params = new HashMap()
				}
				attrs.params.putAll(params.clone())
			}
			def currentSkin = CgrailsUtils.getSkin()
			def viewPath = "/pages/" + currentSkin + "/" + attrs.view
			def fullViewPath= grailsAttributes.getViewUri(viewPath,request)
			fullViewPath = fullViewPath.replaceAll(".gsp.gsp", ".gsp")
			currentSkin = skinningFallbackService.getResourceFallbackSkin(fullViewPath,currentSkin)
			attrs.view = "/pages/" + currentSkin + "/" + attrs.view
		}
		def renderTagLib = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.RenderTagLib')
		renderTagLib.include.call(attrs,body)
	}
	
}
