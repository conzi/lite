package org.xidea.lite.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xidea.el.json.JSONEncoder;
import org.xidea.lite.Template;
import org.xidea.lite.impl.HotTemplateEngine;
import org.xidea.lite.parse.ParseContext;

public class TemplateCompilerEngine extends HotTemplateEngine {

	private Map<String, List<Object>> itemsMap = new HashMap<String, List<Object>>();
	private Map<String, File[]> filesMap = new HashMap<String, File[]>();
	private File webBase;

	public TemplateCompilerEngine(File root) throws IOException {
		super(root.toURI(),new File(root,"/WEB-INF/lite.xml").toURI());
		this.webBase = root.getCanonicalFile();
	}

	@Override
	protected ParseContext createParseContext(String path) {
		ParseContext context = super.createParseContext(path);

		return context;
	}

	public String getLiteCode(String path) {
		this.getTemplate(path);//确保模板初始化
		return buildLiteCode(path,itemsMap.get(path).toArray());
	}

	/**
	 * 只能是合法节点（Object[]）和字符串（String）
	 * @param path
	 * @param items
	 * @return
	 */
	String buildLiteCode(String path, Object... items) {
		return JSONEncoder.encode(new Object[] { getResources(path),items});
	}

	protected List<String> getResources(String path) {
		String root = webBase.getAbsolutePath();
		if (root.endsWith("/") || root.endsWith("\\")) {
			root = root.substring(0, root.length() - 1);
		}
		ArrayList<String> fileList = new ArrayList<String>();
		File[] list = filesMap.get(path);
		if (list == null) {
			fileList.add(path);
		} else {
			for (File file : list) {
				String item = file.getAbsolutePath();
				if (item.startsWith(root)) {
					fileList.add(item.substring(root.length()).replace('\\',
							'/'));
				}
			}
		}
		return fileList;
	}

	protected Template createTemplate(String path,ParseContext context) {
		try{
			return super.createTemplate(path,context);
		}finally{
			this.itemsMap.put(path, context.toList());
			this.filesMap.put(path, this.getAssociatedFiles(context));
		}
	}


}
