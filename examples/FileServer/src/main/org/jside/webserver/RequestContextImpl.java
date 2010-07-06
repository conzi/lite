package org.jside.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RequestContextImpl implements RequestContext {
	private static final Log log = LogFactory.getLog(RequestContextImpl.class);
	private static final String CONTENT_LENGTH = "Content-Length";
	private static final String ISO_8859_1 = "ISO-8859-1";

	private String encoding;
	private String requestURI = "/";
	private ArrayList<String> requestHeaders = new ArrayList<String>();
	private ParamsMap paramsMap;
	private BufferedReader in;
	private ResponseOutputStream out;
	private WebServer server;
	private String requestLine;
	private String method;
	private String query;
	private Map<String, Object> contextMap=new HashMap<String, Object>();
	private Socket remote;
	public BufferedReader getInput(){
		return in;
	}

	RequestContextImpl(WebServer server,Socket remote) throws IOException {

		InputStream sin = remote.getInputStream();
		OutputStream out = remote.getOutputStream();
		this.remote = remote;
		this.encoding = server.getEncoding();
		this.server = server;
		try {
			this.in = new BufferedReader(
					new InputStreamReader(sin, ISO_8859_1));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		this.out = new ResponseOutputStream(this,out);
		this.server = server;
		try {
			requestLine = this.in.readLine();
			String[] rls = requestLine.split("[\\s]");
			method = rls[0];
			requestURI = rls[1];
			if(log.isDebugEnabled()){
				log.debug("process:"+requestURI);
			}
			int p = requestURI.indexOf('?');
			if (p > 0) {
				query = requestURI.substring(p + 1);
				requestURI = requestURI.substring(0, p);
			}
			parseHeaders(this.in);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<String, Object> getApplication() {
		return server.getApplication();
	}

	public String getRequestURI() {
		return requestURI;
	}


	public String getMethod() {
		return method;
	}

	public String getEncoding() {
		return encoding;
	}
	public String getQuery() {
		return query;
	}

	public void setEncoding(String encoding) {
		if(paramsMap!=null){
			paramsMap.reset(encoding);
		}
		this.encoding = encoding;
	}

	public List<String> getRequestHeaders() {
		return requestHeaders;
	}

	@Override
	public Map<String, String[]> getParams() {
		if (paramsMap == null) {
			paramsMap = new ParamsMap();
			paramsMap.parse(query);
			String contentLength = getRequestHeader(CONTENT_LENGTH);
			if (contentLength != null) {
				try {
					String post = getPost(in, Integer.parseInt(contentLength));
					paramsMap.parse(post);
				} catch (Exception e) {
					log.warn(e);
				}
			}
			paramsMap.reset(this.getEncoding());
		}
		return paramsMap;
	}

	public Map<String, String> getParam() {
		return ((ParamsMap)getParams()).toParam();
	}

	public String getRequestHeader(String key) {
		return findHeader(requestHeaders, key);
	}

	private String findHeader(List<String> headers, String key) {
		for (String h : headers) {
			int p = h.indexOf(':');
			if (key.regionMatches(true,0,h,0,key.length())) {
				return h.substring(p + 1).trim();
			}
		}
		return null;
	}

	public OutputStream getOutputStream() {
		return out;
	}

	public String getParameter(String name) {
		String[] values = getParams().get(name);
		return values != null ? values[0] : null;
	}

	public void dispatch(String path) {
		String preuri = this.requestURI;
		this.requestURI = path;
		try {
			server.processRequest(this);
		} catch (Exception e) {
			log.warn("重定向处理失败...", e);
		} finally {
			this.requestURI = preuri;
			try {
				this.getOutputStream().flush();
			} catch (IOException e) {
			}
		}

	}

	private String parseHeaders(BufferedReader in) {
		try {
			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				} else if (line.length() == 0) {
					break;
				} else {
					this.requestHeaders.add(line);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return null;
	}



	private String getPost(Reader in, int contentLength) throws IOException {
		StringBuffer buf = new StringBuffer();
		int b;
		while (contentLength > 0 && (b = in.read()) > -1) {
			contentLength--;
			buf.append((char) b);
		}
		return buf.toString();
	}


	@Override
	public URI getResource(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return server.getWebBase().resolve(path);
	}
	@Override
	public InputStream openStream(URI url){
		try {
			if("file".equals(url.getScheme())){
				File file = new File(URLDecoder.decode(url.getPath(),"UTF-8"));
				if(file.exists()){
					return new FileInputStream(file);
				}
				return null;
			}
			String host = getRequestHeader("Host");
			return getClassResource(host);
		} catch (IOException e) {
			log.warn(e);
			return null;
		}
	}
	private InputStream getClassResource(String host) {
		int p = host.indexOf(':');
		if(p>0){
			host = host.substring(0,p);
		}
		String[] data = host.split("[\\.]");
		StringBuilder buf = new StringBuilder();
		p = data.length;
		while (p-- > 0) {
			buf.append(data[p]);
			buf.append('/');
		}
		buf.append("web/");
		String name = buf.toString();
		return server.getClass().getClassLoader().getResourceAsStream(name);
	}


	public void addResponseHeader(String value) {
		out.addHeader(value);
	}

	@Override
	public void setResponseHeader(String value) {
		out.setHeader(value);
	}

	@Override
	public void setStatus(int status, String message) {
		if(message!=null){
			try {
				message = URLEncoder.encode(message,"UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		}
		out.status = status + " " + message;
	}

	@Override
	public void setMimeType(String mimeType) {
		out.setMimeType(mimeType);
	}

	@Override
	public boolean isAccept() {
		return out.headers == null;
	}

	@Override
	public Map<String, Object> getContextMap() {
		return this.contextMap;
	}

	public void close() throws IOException {
		getOutputStream().flush();
		this.in.close();
		this.out.close();
		this.remote.close();
	}

}

