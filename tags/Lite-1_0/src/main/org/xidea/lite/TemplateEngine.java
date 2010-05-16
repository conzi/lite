package org.xidea.lite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xidea.el.json.JSONDecoder;

public class TemplateEngine {
	private static final Log log = LogFactory.getLog(TemplateEngine.class);
	protected URI baseURI;
	protected File baseFile;
	protected Map<String, Template> templateMap = new java.util.HashMap<String, Template>();

	public TemplateEngine(File base) {
		this.baseFile = base;
		this.baseURI = base.toURI();
	}

	public TemplateEngine(URI base) {
		this.baseURI = base;
	}

	public void render(String path, Object context, Writer out)
			throws IOException {
		getTemplate(path).render(context, out);
		out.flush();
	}

	public Template getTemplate(String path) {
		Template o = templateMap.get(path);
		if (o == null) {
			o = createTemplate(path);
			templateMap.put(path, o);
		}
		return (Template) o;
	}

	@SuppressWarnings("unchecked")
	protected Template createTemplate(String path) {
		try {
			URI url = getResource(path.replace('/', '.'));
			InputStream in = url.toURL().openStream();
			try {
				List data = (List) JSONDecoder.decode(loadText(in, "utf-8"));
				return new Template((List) data.get(1));
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected URI getResource(String path) {
		try {
			if (baseFile != null) {
				// path 必须是 /开头。
				File file = new File(baseFile, path);
				if (file.exists()) {
					return file.toURI();
				}
			}
			if (baseURI != null) {
				return new URL(baseURI.toURL(), path).toURI();

			}
		} catch (Exception e) {
			log.warn(e);
		}
		return null;
	}

	static String loadText(InputStream in, String encoding) throws IOException {
		InputStreamReader reader = new InputStreamReader(in, encoding);
		StringBuilder buf = new StringBuilder();
		char[] cbuf = new char[256];
		for (int len = reader.read(cbuf); len > 0; len = reader.read(cbuf)) {
			buf.append(cbuf, 0, len);
		}
		return buf.toString();
	}
}