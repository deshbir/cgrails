ant.echo("***********Started installing CgrailsDecoratorMapper for sitemesh.......")
def sitemeshXMLFile = "${basedir}/web-app/WEB-INF/sitemesh.xml"
def sitemesh = new XmlParser().parse(sitemeshXMLFile)
sitemesh."decorator-mappers"."mapper"[0].@class = "com.compro.cgrails.sitemesh.FallbackDecoratorMapper"
new XmlNodePrinter(new PrintWriter(new FileWriter(sitemeshXMLFile))).print(sitemesh)
ant.echo("***********Finished installing CgrailsDecoratorMapper for sitemesh********")