package com.compro.cgrails
class SinglepageController {	
	
	def index() {
		render (view:"page1",model:[:])
	}
	
	def menu2() {
		render (view:"page2",model:[:])
	}
}
