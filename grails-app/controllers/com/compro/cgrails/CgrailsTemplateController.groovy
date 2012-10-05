package com.compro.cgrails

class CgrailsTemplateController {
	static allowedMethods = [index: "POST"]
	
	def index() {
		render (view:"/"+ params.path,model:[:])
	}
}
