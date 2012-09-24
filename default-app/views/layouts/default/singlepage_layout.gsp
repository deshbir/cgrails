<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
	    <r:require modules="cgrailsLibs,module1,module2"/>	
	    <g:render template="/initialize" plugin="cgrails"/>
	    <g:layoutHead/>
		<r:layoutResources/>		
	</head>
	<body>
		<g:include view="layout-helpers/header.gsp"/>
		<div id="main_container" class="container">
		 	<h2>Layout 1</h2>
			<g:layoutBody/>
		</div>
		<g:include view="layout-helpers/footer.gsp"/>
		<r:layoutResources/>	
	</body>
</html>