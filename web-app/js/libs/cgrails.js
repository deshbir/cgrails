com.compro.cgrails.utils = (function () 
{

    /********************************************************/
    /*                PRIVATES DATA MEMBERS                 */ 
    /********************************************************/
    var config = {
        OFFLINE_WORKLFLOW_FLAG : "offline"
    };

    /********************************************************/
    /*                 PRIVATES METHODS                     */ 
    /********************************************************/
    var getCurrentSkin = function () {
        return com.compro.cgrails.SKIN ;
    };

    var translateResourceURL = function (resourceURI) {
    	var uri = resourceURI;
    	if(resourceURI.charAt(0) == "/") {
    		uri = resourceURI.substring(1);
    	}
        if(com.compro.cgrails.WORKFLOW != config.OFFLINE_WORKLFLOW_FLAG) {
        	return "/" + com.compro.cgrails.APPLICATIONNAME + "/" + uri ;
        } else {
        	return uri ;
        }
    };
    
    /********************************************************/
    /*                 ONE TIME INIT FUNCTIONS              */ 
    /********************************************************/
    function initializeModule() {
        //Do Something Initialization here
    };


    /********************************************************/  
    /*                 PUBLIC METHODS                       */ 
    /********************************************************/
    return {
        getSkin: getCurrentSkin,
        resource: translateResourceURL
    };


}());