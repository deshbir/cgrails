com.compro.cgrails.preloadData = function () {
	jQuery.each(com.compro.cgrails.PreloadedModel, function(url, objCollection) {
		var store = new Backbone.LocalStorage.preloadStore(url);
		if (jQuery.isArray(objCollection)) {
			jQuery.each(objCollection, function() {
				store.create(this);
			});
		} else {
			store.create(this);
		}		
	});
};
