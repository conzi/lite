package org.xidea.webwork;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xidea.lite.TemplateEngine;
import org.xidea.lite.parser.DecoratorMapper;


public class WebworkTempateServlet extends GenericServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory
			.getLog(WebworkTempateServlet.class);

	private TemplateEngine templateEngine;

	public WebworkTempateServlet() {
	}

	protected Map<Object, Object> createDefaultModel(
			final HttpServletRequest req) {
		OgnlContext context = (OgnlContext) com.opensymphony.xwork.ActionContext.getContext().getValueStack().getContext();
		return new OgnlContextMap(context);
	}

	@Override
	public void service(ServletRequest req, ServletResponse resp)
			throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest) req;
		String path = request.getServletPath();
		templateEngine.render(path, createDefaultModel(request), resp
				.getWriter());
	}

	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		templateEngine = new ServletTemplateEngine(config.getServletContext());

	}

	static class ServletTemplateEngine extends TemplateEngine {
		private ServletContext context;
		public ServletTemplateEngine(ServletContext context) {
			this.context = context;
			try {
				String decoratorPath = context
						.getInitParameter("decoratorMapping");
				if (decoratorPath == null) {
					decoratorPath = DEFAULT_DECORATOR_MAPPING;
				}
				this.decoratorMapper = new DecoratorMapper(context
						.getResourceAsStream(decoratorPath));
			} catch (Exception e) {
				log.error("装载页面装饰配置信息失败", e);
			}
		}

		protected URL getResource(String pagePath) throws MalformedURLException {
			return new File(context.getRealPath(pagePath)).toURI().toURL();
		}
	}

	static class OgnlContextMap extends HashMap<Object, Object> {
		private static final long serialVersionUID = 1L;
		private OgnlContext context;

		public OgnlContextMap(OgnlContext context) {
			this.context = context;
		}

		public Object get(Object key) {
			if (super.containsKey(key)) {
				return super.get(key);
			} else if (key instanceof String) {
				try {
					return OgnlRuntime.getProperty(context, context.getRoot(),
							key);
				} catch (OgnlException e) {
					return null;
				}
				// return stack.findValue((String) key);
			}
			return super.get(key);
		}
	}
}
