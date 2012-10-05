package com.compro.cgrails

class TemplateController {
	static allowedMethods = [index: "POST"]
	
	def index() {
		render (view:"/"+ params.path,model:[:])
	}
}
