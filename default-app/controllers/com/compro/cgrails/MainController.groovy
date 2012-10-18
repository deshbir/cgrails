package com.compro.cgrails

class MainController {
	def index() {
		redirect (uri:"/" + CgrailsUtils.getSkin() + "/singlepage/index")
		return
	}
	
}
