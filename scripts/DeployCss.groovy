import grails.util.GrailsUtil	

ant.project.getBuildListeners().each{
	if(it.metaClass.respondsTo(it, "setMessageOutputLevel")){
		it.setMessageOutputLevel(ant.project.MSG_INFO)
	}
}

target(deployCSS:"Deploys CSS files") {	
	String className = 'com.compro.cgrails.CgrailsConstants'
	def classLoader = Thread.currentThread().contextClassLoader
	classLoader.addURL(new File(classesDirPath).toURI().toURL())
	def Constants = Class.forName(className, true, classLoader).newInstance()
	def config
	try {
		config = new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('CgrailsConfig'))
	} catch (ClassNotFoundException e) {
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
	} else {
	    ant.echo("*********** Started deploying CSS.***********");
		compileLESS(argsMap, Constants, config);
		generateRTLCSS(argsMap, Constants, config);
		ant.echo("*********** CSS deployed successfully.***********");
		ant.echo("****************************************************************************");
	} 		
	
}

private compileLESS(def argsMap, def Constants, def config) {
	ant.echo("Compiling LESS into CSS..........")
	if(argsMap.skin){
		runCompileLess(argsMap.skin,Constants,config)
	} else {
		def skins = config.cgrails.skinning.skins
		skins.put(config.cgrails.skinning.baseskin,"")
		skins.each {skin->
			String skinname = skin.key
			runCompileLess(skinname,Constants,config)
		}
		
	}
	ant.echo("Successfully Compiled LESS into CSS.")
}

private generateRTLCSS(def argsMap, def Constants, def config) {
	ant.echo("Generating RTL CSS...........")	
	if(argsMap.skin){
		runRTLCompileLess(argsMap.skin,Constants,config)
	} else {
		def skins = config.cgrails.skinning.skins
		skins.put(config.cgrails.skinning.baseskin,"")
		skins.each{skin->
			String skinname = skin.key
			runRTLCompileLess(skinname,Constants,config)
		}
	}
	ant.echo("Successfully generated RTL CSS.")
}		

private runCompileLess(def skinname, def Constants, def config){
	def lessFileArray = config.cgrails.less.files
	String cssDir = Constants.CGRAILS_CSS_FOLDER_LOCATION
	lessFileArray.each {lessFilename->
		String inputFilePath = "${cssDir}/${skinname}/${Constants.LESS_FOLDER_NAME}/${lessFilename}.less"
		File inputFile = new File(inputFilePath)
		if(inputFile.exists()) {
			String ouputFilePath = "${cssDir}/${skinname}/${lessFilename}.css"
			File ouputFile = new File(ouputFilePath)
			if(ouputFile.exists()) {
				ant.delete(file:"${ouputFilePath}")
			}
			ant.echo("Compiling ${inputFilePath} to ${ouputFilePath}")
			ant.exec(failonerror: "false",executable:"cmd"){
				arg(value:"/c")
				arg(value:"cscript")
				arg(value:"//nologo")
				arg(value:"${cgrailsPluginDir}/" + Constants.LESS_BUILDCOMPILER_PATH)
				arg(value:"${inputFilePath}")
				arg(value:"${ouputFilePath}")
				//arg(value:"-compress")
			}
		}
	}
}
private runRTLCompileLess(def skinname, def Constants, def config){
	String cssDir = Constants.CGRAILS_CSS_FOLDER_LOCATION
	def cssFileArray = config.cgrails.less.files
	cssFileArray.each {cssFileName->
		String inputFilePath = "${cssDir}/${skinname}/${cssFileName}.css"
		File inputFile = new File(inputFilePath)
		if(inputFile.exists()) {
			String ouputFilePath = "${cssDir}/${skinname}/${cssFileName}-rtl.css"
			File ouputFile = new File(ouputFilePath)
			if(ouputFile.exists()) {
				ant.delete(file:"${ouputFilePath}")
			}
			ant.echo("Compiling ${inputFilePath} to ${ouputFilePath}")
			ant.exec(executable:"cmd"){
				arg(value:"/c")
				arg(value:"r2")
				arg(value:"${inputFilePath}")
				arg(value:"${ouputFilePath}")
			}
		}
	}
}
setDefaultTarget(deployCSS)
	
	

