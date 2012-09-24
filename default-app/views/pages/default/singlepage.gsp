<html>
    <head>
    	<title>Cgrails Hello world</title>
        <meta name="layout" content="singlepage_layout" />
		<cgrails:stylesheet src="styles" watch="false"/>
    </head>
    <body>
    	<div id="sub_container"></div>
    	<r:script type="text/javascript">
			View1.routerInitialize();
			View2.routerInitialize();
			Backbone.history.start();
			if (window.location.hash.length <= 1){
				Backbone.history.navigate("#/menu1", {trigger:true,replace:true});
			} 	
		</r:script>	
    </body>
</html>

