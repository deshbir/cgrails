package com.grailway



class TemplateController {
		
	def index() {
		render (view:"/"+ params.path,model:[:])
	}
}
