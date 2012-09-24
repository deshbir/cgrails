

target(skinSkeleton: "Generates skeleton for skinning in application") {
	depends(cgrailsDecoratorMapper)
	ant.echo("****************************************************************************");
	ant.mkdir(dir: "web-app/css/cgrails/default/less")
	ant.copy(todir: "web-app/css/cgrails/default/less") {
		fileset(dir: "${cgrailsPluginDir}/default-app/css/default")
	}
	ant.mkdir(dir: "web-app/css/cgrails/skin1/less")
	ant.copy(todir: "web-app/css/cgrails/skin1/less") {
		fileset(dir: "${cgrailsPluginDir}/default-app/css/skin1")
	}
	ant.echo("***********Successfully copied skinning folder to application.***************");
 }

target(cgrailsDecoratorMapper: "Installs CgrailsDecoratorMapper for sitemesh in application") {
	depends(createController)
	ant.echo("***********Started installing CgrailsDecoratorMapper for sitemesh.......")
	def sitemeshXMLFile = "${basedir}/web-app/WEB-INF/sitemesh.xml"
	def sitemesh = new XmlParser().parse(sitemeshXMLFile)
	sitemesh."decorator-mappers"."mapper"[0].@class = "com.compro.cgrails.sitemesh.FallbackDecoratorMapper"
	new XmlNodePrinter(new PrintWriter(new FileWriter(sitemeshXMLFile))).print(sitemesh)
	ant.echo("***********Finished installing CgrailsDecoratorMapper for sitemesh********")
 }
 
target(createController: "Copies  MainController for sample skinning") {
	depends(modifyConfig)
	ant.echo("***********Copying  MainController for sample skinning.......")
	ant.mkdir(dir: "grails-app/controllers/com/compro/cgrails")
	ant.copy ( todir : 'grails-app/controllers/com/compro/cgrails' ,  file : "${cgrailsPluginDir}/default-app/controllers/com/compro/cgrails/MainController.groovy" )
	ant.echo("***********Finished installing CgrailsDecoratorMapper for sitemesh********")
 }

target(modifyConfig: "Copies Required config files for skinning") {
	depends(copyJS,copyViews)
	ant.echo("***********Copying CgrailsConfig.groovy for skinning.......")	
	ant.copy(todir: "grails-app/conf") {
		fileset(dir: "${cgrailsPluginDir}/default-app/conf")
	}
 }


target(copyJS: "Copies sampleJS for skinning") {
	ant.echo("***********Copying sampleJS......")
	ant.copy(todir: "web-app/js") {
		fileset(dir: "${cgrailsPluginDir}/default-app/js")
	}
 }
target(copyViews: "Copies sample views for skinning") {
	ant.echo("***********Copying sample views.......")
	ant.copy(todir: "grails-app/views") {
		fileset(dir: "${cgrailsPluginDir}/default-app/views")
	}
 }
setDefaultTarget(skinSkeleton)
