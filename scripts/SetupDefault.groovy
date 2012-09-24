

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
	ant.mkdir(dir: "grails-app/controllers/com/cgrails")
	ant.copy ( todir : 'grails-app/controllers/com/cgrails' ,  file : "${cgrailsPluginDir}/default-app/controllers/MainController.groovy" )
	ant.echo("***********Finished installing CgrailsDecoratorMapper for sitemesh********")
 }

target(modifyConfig: "Copies CgrailsConfig.groovy for skinning") {
	depends(copyJS,copyViews)
	ant.echo("***********Copying CgrailsConfig.groovy for skinning.......")	
	ant.copy ( todir : 'grails-app/conf' ,  file : "${cgrailsPluginDir}/default-app/conf/CgrailsConfig.groovy" )
 }


target(copyJS: "Copies sampleJS for skinningg") {
	ant.echo("***********Copying sampleJS......")
	ant.mkdir(dir: "web-app/js/cgrails")
	ant.copy(todir: "web-app/js/cgrails") {
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
