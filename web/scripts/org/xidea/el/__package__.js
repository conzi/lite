this.addScript('expression-token.js',
				[
					//函数
					"getTokenParam","hasTokenParam","getTokenParamIndex",'getTokenLength','findTokenType','findTokenText'
					,"BIT_*"
					,"VALUE_*"
					,"OP_*"
					]
				,0
				,"evaluate");


this.addScript('js-el-translator.js','ELTranslator'
                ,0
                ,['getTokenParamIndex','getTokenParam','getPriority',"findTokenText","org.xidea.lite.util:stringifyJSON",'ExpressionTokenizer','org.xidea.jsi:$log']);


this.addScript('expression.js',['evaluate','Expression']
                ,['getTokenParam']
                ,"ExpressionTokenizer");

this.addScript('json-tokenizer.js','JSONTokenizer');

this.addScript('expression-tokenizer.js',['getPriority','ExpressionTokenizer']
                ,'JSONTokenizer'
                ,["hasTokenParam",'org.xidea.jsi:$log']);

this.addScript('el-util.js','findELEnd'
                ,0
                ,'org.xidea.jsi:$log');


this.addDependence("*","expression-token.js",true);
