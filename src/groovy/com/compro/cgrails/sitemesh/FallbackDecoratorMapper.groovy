

package  com.compro.cgrails.sitemesh 

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest

import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.web.metaclass.ControllerDynamicMethods
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.codehaus.groovy.grails.web.sitemesh.GrailsLayoutDecoratorMapper
import org.codehaus.groovy.grails.web.sitemesh.GrailsNoDecorator
import org.codehaus.groovy.grails.web.sitemesh.GroovyPageLayoutFinder
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils

import com.compro.cgrails.CgrailsConstants
import com.compro.cgrails.CgrailsUtils
import com.opensymphony.module.sitemesh.Config
import com.opensymphony.module.sitemesh.Decorator
import com.opensymphony.module.sitemesh.DecoratorMapper
import com.opensymphony.module.sitemesh.Page

class FallbackDecoratorMapper extends  GrailsLayoutDecoratorMapper{	
	
	private GroovyPageLayoutFinder groovyPageLayoutFinder;	
	def grailsApplication
	@Override
	public void init(Config c, Properties properties, DecoratorMapper parentMapper) throws InstantiationException {
		
		super.init(c, properties, parentMapper);
		
		ServletContext servletContext = c.getServletContext();
		WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		grailsApplication = applicationContext.getBean("grailsApplication", DefaultGrailsApplication.class);
		groovyPageLayoutFinder = applicationContext.getBean("groovyPageLayoutFinder", GroovyPageLayoutFinder.class);
	}
	
	
	@Override
	public Decorator getDecorator(HttpServletRequest request, Page page) {
		Decorator decorator = null;
		String layoutName = page.getProperty("meta.layout")			
		if (layoutName == null || StringUtils.isBlank(layoutName)) {
			GroovyObject controller = (GroovyObject)request.getAttribute(GrailsApplicationAttributes.CONTROLLER);
			if (controller) {
				
				String actionUri = (String)controller.getProperty(ControllerDynamicMethods.ACTION_URI_PROPERTY);
				String controllerName = (String)controller
									.getProperty(ControllerDynamicMethods.CONTROLLER_NAME_PROPERTY);
				Object layoutProperty = GrailsClassUtils.getStaticPropertyValue(controller.getClass(), "layout");
				if (layoutProperty != null && layoutProperty instanceof CharSequence) {
					decorator = getNamedDecorator(request, layoutProperty.toString());
				} else {					
					if(!StringUtils.isBlank(actionUri)) {							
						decorator = getCustomNamedDecorator(request, actionUri.substring(1));
					} 
					if (decorator == null && !StringUtils.isBlank(controllerName)) {
						 decorator = getCustomNamedDecorator(request, controllerName);
					}		
					if (decorator == null) {
						decorator = getCustomApplicationDefaultDecorator(request)
					}
				}					
			} else {
				decorator = getCustomApplicationDefaultDecorator(request)
			}					
		} else {
			decorator = getNamedDecorator(request,layoutName)
		}			
			
		return decorator
	}

	@Override
	public Decorator getNamedDecorator(HttpServletRequest request, String name) {
		Decorator decorator = getCustomNamedDecorator(request, name)
		return (decorator != null) ? decorator : (new GrailsNoDecorator())
	}
	
	private Decorator getCustomNamedDecorator (HttpServletRequest request, String name) {
		def currentSkin = CgrailsUtils.getSkin()
		
		def fulllayoutPath = currentSkin  + "/" + name
		def decorator = groovyPageLayoutFinder.getNamedDecorator(request,fulllayoutPath)
		
		//falls back to parent
		while (decorator == null && (currentSkin != grailsApplication.config.cgrails.skinning.baseskin)) {
			def parentSkin = grailsApplication.config.cgrails.skinning.skins."${currentSkin}".parent
			fulllayoutPath = fulllayoutPath.replaceFirst(currentSkin, parentSkin)
			decorator = groovyPageLayoutFinder.getNamedDecorator(request,fulllayoutPath)
			currentSkin = parentSkin
		}
		return decorator;
	}
	
	private Decorator getCustomApplicationDefaultDecorator(HttpServletRequest request) {
		def layoutNameConfig = grailsApplication.config.grails.sitemesh?.default?.layout
		String layoutName = layoutNameConfig ? layoutNameConfig : "application"	
		Decorator decorator = getCustomNamedDecorator(request,layoutName)
		if(decorator == null) {
			return (new GrailsNoDecorator());
		}
		return decorator
	}
}


