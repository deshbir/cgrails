package com.compro.cgrails.service

import org.springframework.web.context.request.RequestContextHolder


public class DefaultCgrailsServiceImpl implements CgrailsService {
	
	def grailsApplication
	
	public String getSkin() {
		HashMap<?,?> paramsMap = RequestContextHolder.currentRequestAttributes().params
		String skin = paramsMap["skin"]
		if(skin != null) {
			if ((!grailsApplication.config.cgrails.skinning.skins."${skin}")
				|| (!grailsApplication.config.cgrails.skinning.skins."${skin}".parent)){
				return grailsApplication.config.cgrails.skinning.baseskin
			} else {
				return skin
			}
		} else {
			return grailsApplication.config.cgrails.skinning.baseskin
		}
	}
	
	public String getWorkflow() {
		HashMap<?,?> paramsMap = RequestContextHolder.currentRequestAttributes().params
		String workflow = paramsMap["workflow"]
		return workflow
	}
	
}
