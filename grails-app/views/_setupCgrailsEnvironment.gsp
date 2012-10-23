<%@ page import="com.compro.cgrails.CgrailsUtils" %>
<%@ page import="com.compro.cgrails.CgrailsConstants" %>

<g:set var="contextPath" value="${request.contextPath.substring(1)}"/>
<g:set var="workflow" value="${CgrailsUtils.getWorkflow()}"/>
<g:set var="skin" value="${CgrailsUtils.getSkin()}"/>

<r:script type="text/javascript" disposition="head">
	var com = {
		 compro : {
			cgrails : {
				APPLICATIONNAME : "${contextPath}",
				WORKFLOW : "${workflow}",
				SKIN : "${skin}"
			}
		}
	}			
</r:script>
<g:if test="${workflow == CgrailsConstants.WORKFLOW_OFFLINE}">
	<r:require module="offline"/>
	<r:script type="text/javascript" disposition="defer">
		if(window.parentSandboxBridge!=undefined){
			localStorage = window.parentSandboxBridge;
		}	
		localStorage.clear();	
		com.compro.cgrails.preloadData();
	</r:script>	
	<g:if test="${params.mode == "debugAir"}">
		<r:require module="debugAir"/>	
	</g:if>	
</g:if>
