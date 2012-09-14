package com.compro.cgrails

class OverrideGrailsTagLib {
	
	static namespace = "g"
	
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
			def resource = grailsAttributes.getPagesTemplateEngine().getResourceForUri(fullViewPath)
			// if view does not exist in current skin , fall back to parent skin
			while (!resource.exists() && (currentSkin != grailsApplication.config.cgrails.skinning.baseskin)) {
				def parentSkin = grailsApplication.config.cgrails.skinning.skins."${currentSkin}".parent
				fullViewPath = fullViewPath.replaceFirst(currentSkin, parentSkin)
				resource = grailsAttributes.getPagesTemplateEngine().getResourceForUri(fullViewPath)
				currentSkin = parentSkin
			}
			attrs.view = "/pages/" + currentSkin + "/" + attrs.view
		}
		def renderTagLib = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.RenderTagLib')
		renderTagLib.include.call(attrs,body)
	}
	
}
