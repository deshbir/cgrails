<!doctype html>
<html>
	<head>		
		<title>Cgrails Hello world</title>
		<meta name="layout" content="layout2">	
		<cgrails:stylesheet src="styles" watch="false"/>	
	</head>
	<body>
		This is page 2 of default skin.
		<div id="sub_container"></div>
		<r:script type="text/javascript">
			View2.routerInitialize();
			Backbone.history.start();
			if (window.location.hash.length <= 1){
				Backbone.history.navigate("#/menu2", {trigger:true,replace:true});
			} 	
		</r:script>	
	<body/>
</html>