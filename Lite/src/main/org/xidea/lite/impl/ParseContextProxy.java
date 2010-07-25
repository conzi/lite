package org.xidea.lite.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xidea.el.ExpressionFactory;
import org.xidea.lite.parse.ParseConfig;
import org.xidea.lite.parse.ParseContext;
import org.xidea.lite.parse.ResultContext;
import org.xml.sax.SAXException;

abstract public class ParseContextProxy implements ParseContext {
	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(ParseContextProxy.class);
	

	/**
	 * 外部不允许修改
	 */
	private final Map<String, String> featrueMap;

	
	protected ResultContext resultContext;
	protected ParseConfig config;

	
	protected ParseContextProxy(ParseConfig config,Map<String, String> featrueMap) {
		this.config = config;
		this.featrueMap = featrueMap;
		this.resultContext = new ResultContextImpl(this);
	}

	public ParseContextProxy(ParseContext parent) {
		// 需要重设 ParseChain 的context
		this.config = parent;
		this.featrueMap =  parent.getFeatrueMap();
		this.resultContext = parent;
	}


	public String getFeatrue(String key) {
		return featrueMap.get(key);
	}

	public Map<String, String> getFeatrueMap() {
		return featrueMap;
	}

	public final URI createURI(String path) {
		try {
			// TODO
			URI parent = this.getCurrentURI();
			return parent.resolve(path);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public final InputStream openStream(URI uri) {
		if("lite".equals(uri.getScheme())){
			String path = uri.getPath();
			if(path.startsWith("/")){
				path = path.substring(1);
			}
			uri = config.getRoot().resolve(path);
		}
		return ParseUtil.openStream(uri);
	}


	public final Document loadXML(URI uri) throws SAXException, IOException {
		return ParseUtil.parse(uri, (ParseContext) this);
	}

	public final String getDecotatorPage(String path) {
		return config.getDecotatorPage(path);
	}

	public final Map<String, String> getFeatrueMap(String path) {
		return config.getFeatrueMap(path);
	}


	public final URI getRoot() {
		return config.getRoot();
	}

	public Map<String, List<String>> getExtensions(String path) {
		return config.getExtensions(path);
	}


	public final String allocateId() {
		return resultContext.allocateId();
	}

	public final void append(String text) {
		resultContext.append(text);
	}

	public final void append(String text, boolean encode, char escapeQute) {
		resultContext.append(text, encode, escapeQute);
	}

	public final void appendAll(List<Object> instruction) {
		resultContext.appendAll(instruction);
	}

	public final void appendXA(String name, Object el) {
		resultContext.appendXA(name, el);
	}

	public final void appendXT(Object el) {
		resultContext.appendXT(el);
	}

	public final void appendCaptrue(String varName) {
		resultContext.appendCaptrue(varName);
	}

	public final void appendEL(Object el) {
		resultContext.appendEL(el);
	}

	public final void appendElse(Object testEL) {
		resultContext.appendElse(testEL);
	}

	public final int appendEnd() {
		return resultContext.appendEnd();
	}

	public final void appendFor(String var, Object itemsEL, String status) {
		resultContext.appendFor(var, itemsEL, status);
	}

	public final void appendIf(Object testEL) {
		resultContext.appendIf(testEL);
	}

	public final void appendVar(String name, Object valueEL) {
		resultContext.appendVar(name, valueEL);
	}


	public final void appendPlugin(String pluginClazz, Object el) {
		resultContext.appendPlugin(pluginClazz, el);
	}

	public final int mark() {
		return resultContext.mark();
	}

	public final List<Object> reset(int mark) {
		return resultContext.reset(mark);
	}

	public final int getDepth() {
		return resultContext.getDepth();
	}

	public final int getType(int offset) {
		return resultContext.getType(offset);
	}

	public final boolean isReserveSpace() {
		return resultContext.isReserveSpace();
	}

	public final void setReserveSpace(boolean keepSpace) {
		resultContext.setReserveSpace(keepSpace);
	}

	public final List<Object> toList() {
		return resultContext.toList();
	}

//	public final String toResult() {
//		return resultContext.toResult();
//	}

	/**
	 * 自定义表达式解析器
	 * 
	 * @param expressionFactory
	 */
	public final void setExpressionFactory(ExpressionFactory expressionFactory) {
		resultContext.setExpressionFactory(expressionFactory);
	}

	public final Object parseEL(String eltext) {
		return resultContext.parseEL(eltext);
	}

	public final void addResource(URI resource) {
		resultContext.addResource(resource);

	}

	public final URI getCurrentURI() {
		return resultContext.getCurrentURI();
	}

	public final Collection<URI> getResources() {
		return resultContext.getResources();
	}

	public final void setCurrentURI(URI currentURI) {
		resultContext.setCurrentURI(currentURI);
	}

	public final <T> T getAttribute(Object key) {
		return resultContext.getAttribute(key);
	}

	public final void setAttribute(Object key, Object value) {
		resultContext.setAttribute(key, value);
	}

	public final int getTextType() {
		return resultContext.getTextType();
	}

	public final void setTextType(int textType) {
		resultContext.setTextType(textType);
	}

}
