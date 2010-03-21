package org.xidea.lite.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;
import org.xidea.lite.parser.impl.HotTemplateEngine;

public class TemplateEngineSerializeTest {
	//@Test
	public void test() throws IOException, ClassNotFoundException{
		Object[] obj = new Object[]{new HotTemplateEngine(new File("."))};
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream out2 = new ObjectOutputStream(out);
		out2.writeObject(obj);
		out2.flush();
		ObjectInputStream in =new ObjectInputStream( new ByteArrayInputStream(out.toByteArray()));
		Object[] obj2 =(Object[])in.readObject();
		HotTemplateEngine engine = (HotTemplateEngine)obj2[0];
		//System.out.println(engine.parser);
		
		
	}

}
