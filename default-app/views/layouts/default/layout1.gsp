<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
	   <title>Cgrails Sample Application</title>
	    <cgrails:stylesheet src="styles"/>
	    <r:require modules="cgrailsLibs,module1"/>	
	    <cgrails:environment_setup/>
	    <g:layoutHead/>
		<r:layoutResources/>		
	</head>
	<body>
		<g:include view="layout-helpers/header.gsp"/>
		<h2>Layout 1</h2>
		<div id="main_container" class="container">
			<g:layoutBody/>
		</div>
		<g:include view="layout-helpers/footer.gsp"/>
		<r:layoutResources/>	
	</body>
</html>