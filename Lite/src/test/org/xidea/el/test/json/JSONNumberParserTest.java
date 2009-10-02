package org.xidea.el.test.json;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xidea.el.impl.ExpressionFactoryImpl;
import org.xidea.el.impl.ExpressionImpl;

public class JSONNumberParserTest {

	@Before
	public void setUp() throws Exception {
	}
	@Test
	public void testNumber(){
		doTestNumber(0,"0.0");
		doTestNumber(1234,"1234.intValue()");
		doTestNumber(2,"2.23.intValue()");
		doTestNumber(223,"2.23E2");
		doTestNumber(22.3,"2.23E1");
		doTestNumber(0.223,"2.23E-1");
		doTestNumber(1,"1");
		doTestNumber(0,"0");
		doTestNumber(1.23,"1.23");
		doTestNumber(0.23,"0.23");
		doTestNumber(223,"2.23E2");
		doTestNumber(0x23,"0x23");
		doTestNumber(0x23,"0x23.intValue()");
		System.out.println(Double.parseDouble("2.3E4"));
	}

	public void doTestNumber(Object expected,String json){
		Assert.assertEquals(expected, ((ExpressionImpl)(ExpressionFactoryImpl.getInstance().create(json))).evaluate(null));
		Assert.assertEquals(expected, ((ExpressionImpl)ExpressionFactoryImpl.getInstance().create(json+" ")).evaluate(null));
	}
}
