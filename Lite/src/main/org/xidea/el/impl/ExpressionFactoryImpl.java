package org.xidea.el.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


import org.xidea.el.OperationStrategy;
import org.xidea.el.Expression;
import org.xidea.el.ExpressionFactory;
import org.xidea.el.ExpressionToken;
import org.xidea.el.fn.ECMA262Impl;

public class ExpressionFactoryImpl implements ExpressionFactory {
	public static final OperationStrategy DEFAULT_CALCULATER = new OperationStrategyImpl();
	public static final Map<String, Object> DEFAULT_GLOBAL_MAP;
	
	private static ExpressionFactoryImpl expressionFactory = new ExpressionFactoryImpl();
	static {
		HashMap<String, Object> global = new HashMap<String, Object>();
		ECMA262Impl.setup(global);
		DEFAULT_GLOBAL_MAP = Collections.unmodifiableMap(global);
		expressionFactory.addOperatorAlias("!","not");//取非
		expressionFactory.addOperatorAlias(">", "gt");//大于;
		expressionFactory.addOperatorAlias("<","lt");//小于
		expressionFactory.addOperatorAlias(">=","ge");//大于等于
		expressionFactory.addOperatorAlias("<=","le");//小于等于
		expressionFactory.addOperatorAlias("==","eq");//等于
		expressionFactory.addOperatorAlias("!=","ne");//不等于
		expressionFactory.addOperatorAlias("/","div");//除
		expressionFactory.addOperatorAlias("%","mod");//取余数
		expressionFactory.addOperatorAlias("&&","and");//且
		expressionFactory.addOperatorAlias("||","or");//或
	}
	protected Map<String, Object> globals = DEFAULT_GLOBAL_MAP;
	protected final Map<String,Integer> operatorAliasMap = new HashMap<String, Integer>();
	protected final Map<Object, Expression> cached = new WeakHashMap<Object, Expression>();
	
	public static ExpressionFactory getInstance() {
		return expressionFactory;
	}

	public ExpressionFactoryImpl() {
	}

	public ExpressionFactoryImpl(Map<String, Object> globals) {
		this.globals = globals;
	}

	public Object parse(String el) {
		ExpressionToken tokens = new ExpressionTokenizer(el,operatorAliasMap).getResult();
		return tokens;
	}

	@SuppressWarnings("unchecked")
	public Expression create(Object elo) {
		if (elo instanceof String) {
			Expression el = cached.get(elo);
			if(el == null){
				el = create(parse((String) elo));
				cached.put(elo, el);
			}
			return el;
		} else {
			ExpressionToken el;
			if (elo instanceof ExpressionToken) {
				el = (ExpressionToken) elo;
			} else 
			{
				el = TokenImpl.toToken((List<Object>) elo);
			}
			return getOptimizedExpression(el);

		}
	}
	public void addOperatorAlias(Object op,String... alias){
		if(op instanceof String){
			op = new TokenImpl((String)op).getType();
		}
		if(op!= null){
			for(String as:alias){
				this.operatorAliasMap.put(as,(Integer)op);
			}
		}
		
	}
	

	private Expression getOptimizedExpression(ExpressionToken el) {
		Expression ressult = OptimizeExpressionImpl.create(this,el,
				DEFAULT_CALCULATER, globals);
		return ressult != null ? ressult : new ExpressionImpl(null, el,
				DEFAULT_CALCULATER, globals);
	}
}
