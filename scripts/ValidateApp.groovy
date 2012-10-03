import grails.util.GrailsUtil	

ant.project.getBuildListeners().each{
	if(it.metaClass.respondsTo(it, "setMessageOutputLevel")){
		it.setMessageOutputLevel(ant.project.MSG_INFO)
	}
}

target(validateApp:"Validates Application For Configuration Errors") {	
	def classLoader = Thread.currentThread().contextClassLoader
	def config
	try {
		config = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('CgrailsConfig'))
	} catch (Exception e) {
		ant.echo("********ERROR*************");
		ant.echo("Cgrails configuration file not found. Please add CgrailsConfig.groovy file.");
		return
		
	}
	if (!config.cgrails?.skinning){
		ant.echo("********ERROR*************");
		ant.echo("Cgrails Skinning configuration not found.");
		exit(1)
	} else if (!config.cgrails?.less){
		ant.echo("********ERROR*************");
		ant.echo("Cgrails LESS configuration not found.");
		exit(1)
	} else if(!config.cgrails.skinning.baseskin) {
	    ant.echo("********ERROR*************");
		ant.echo("Application base skin (cgrails.skinning.baseskin) configuration not found.")
		exit(1)
	}		
	
}
setDefaultTarget(validateApp)
	
	

