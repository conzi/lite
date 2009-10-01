package org.xidea.el.operation.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.xidea.el.Expression;
import org.xidea.el.ExpressionFactory;
import org.xidea.el.Invocable;
import org.xidea.el.fn.ECMA262Impl;
import org.xidea.el.impl.ExpressionFactoryImpl;

public class ECMA262FunctionTest {
	ExpressionFactory factory = new ExpressionFactoryImpl();
	Invocable encodeURI = buildInvocable(ECMA262Impl.URI.class, true);
	Invocable decodeURI = buildInvocable(ECMA262Impl.URI.class, false);
	Invocable encodeURIComponent = buildInvocable(
			ECMA262Impl.URIComponent.class, true);
	Invocable decodeURIComponent = buildInvocable(
			ECMA262Impl.URIComponent.class, false);

	static Invocable buildInvocable(Class<? extends Invocable> clz, boolean flag) {
		try {
			Constructor<? extends Invocable> c = clz.getDeclaredConstructor(Boolean.TYPE);
			c.setAccessible(true);
			return c.newInstance(flag);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Test
	public void testEncodeURLComponentEL() throws Exception {
		Expression el = factory.create("encodeURIComponent('金大为')");
		String encoded = (String) el.evaluate(null);
		assertEquals("%E9%87%91%E5%A4%A7%E4%B8%BA", encoded);

		el = factory
				.create("decodeURIComponent('a%E9%87%91%E5%A4%A7%E4%B8%BA')");
		assertEquals("a金大为", el.evaluate(null));
	}

	@Test
	public void testEncodeURLComponent() throws Exception {
		Map<String, String> encodeMap = new LinkedHashMap<String, String>();
		// encodeMap.put("+", "%2B");
		encodeMap.put("金+大+为", "%E9%87%91%2B%E5%A4%A7%2B%E4%B8%BA");
		encodeMap.put("http://www.xidea.org/a b c 金 大 为",
				"http%3A%2F%2Fwww.xidea.org%2Fa%20b%20c%20%E9%87%91%20%E5%A4%A7%20%E4%B8%BA"
						.replace("%20", "+"));
		for (String key : encodeMap.keySet()) {
			String value = encodeMap.get(key);
			assertEquals(value, encodeURIComponent.invoke(null, key));
			assertEquals(key, decodeURIComponent.invoke(null, value));
		}
		// System.out.println(encodeURIComponent.invoke("1 2"));
		// System.out.println(decodeURIComponent.invoke("1+2"));
	}

	@Test
	public void testEncodeURL() throws Exception {
		Map<String, String> encodeMap = new LinkedHashMap<String, String>();
		// encodeMap.put("+", "%2B");
		encodeMap.put("http://www.xidea.org/金+大+为",
				"http://www.xidea.org/%E9%87%91%2B%E5%A4%A7%2B%E4%B8%BA");
		encodeMap.put("http://www.xidea.org/a?金=大&为=2",
				"http://www.xidea.org/a?%E9%87%91=%E5%A4%A7&%E4%B8%BA=2"
						.replace("%20", "+"));
		for (String key : encodeMap.keySet()) {
			String value = encodeMap.get(key);
			assertEquals(value, encodeURI.invoke(null, key));
			assertEquals(key, decodeURI.invoke(null, value));
		}
	}

	@Test
	public void testNumber() throws Exception {
		// Expression el = factory.createEL("'金大为'.substring(1,2)");
		Expression el = factory.create("parseInt(1.23)");
		assertEquals(1, el.evaluate(null));
	}

	@Test
	public void testIsNaN() throws Exception {
		// Expression el = factory.createEL("'金大为'.substring(1,2)");
		Expression el = factory.create("isNaN(0/0)");
		assertEquals(true, el.evaluate(null));
	}

	@Test
	public void testFP() throws Exception {
		// Expression el = factory.createEL("'金大为'.substring(1,2)");
		Expression el = factory.create("(true?isNaN:isFinite)(0/0)");
		assertEquals(true, el.evaluate(null));
		el = factory.create("(false?isNaN:isFinite)(0/0)");
		assertEquals(false, el.evaluate(null));
	}

}
