/*
 * List Template
 * License LGPL(您可以在任何地方免费使用,但请不要吝啬您对框架本身的改进)
 * http://www.xidea.org/project/lite/
 * @author jindw
 * @version $Id: template.js,v 1.4 2008/02/28 14:39:06 jindw Exp $
 */

/**
 * 将Lite中间代码转化为直接的php代码
 * 
 * function index_xhtml_php($__engine,$__context){
 * 	$encodeURIComponent = 'lite_encodeURIComponent';	
 * 	$decodeURIComponent = 'lite_decodeURIComponent';	
 *  $key = null;
 *  $key2 = null;
 *  $test = 'index_xhtml_php__test';
 *  extract($__context);
 *  
 *   
 * }
 * function index_xhtml_php__test($__engine,$arg1,$arg2){
 *  	
 * }
 */

var FOR_STATUS_KEY = '$__for';
var ID_PREFIX = "_$";

//function checkEL(el){
//    new Function("return "+el)
//}

/**
 * JS原生代码翻译器实现
 */
function PHPTranslator(id){
    this.id = id;
}

PHPTranslator.prototype = {
	translate:function(context){
	    //var result =  stringifyJSON(context.toList())
	    var list = context.toList();
		var context = new TranslateContext(list);
		context.parse();
		var code = context.toString();
	    return code;
		
	}
}
function TranslateContext(code,params){
    LiteStatus.apply(this,arguments);
//    this.code = code;
//    this.forInfos = vs.forInfos;
//    this.needReplacer = vs.needReplacer;
//    this.defs = vs.defs;
//    this.refMap = vs.refMap;
    this.idMap = {};
    this.depth = 1;
    //print([vs.defs,vs.refMap])
}
TranslateContext.prototype = {
	stringifyEL:function (el){
		return el?stringifyPHPEL(el.tree):null;
	},
//	parseDef
	parse:function(){
		var code = this.code;
		var params = this.params;
		this.out = [];
		var firstAppend = true;
		this.append("extract($__context__,EXTR_SKIP);");

	    //add function
	    for(var i=0;i<this.defs.length;i++){
	        var def = this.defs[i];
	        var n = def.name;
	        this.append("function lite_def_",n,"(",def.params.join(','),'){')
	        this.depth++;
	        this.append('ob_start();');
	        this.appendCode(def.code);
	        this.append("return _$out.join('');");
	        this.depth--;
	        this.append("}");
	     	this.append('if("',n,'" in _$context){',n,'=_$context["',n,'"];}')
	    }
	    try{
	        this.appendCode(code);
	    }catch(e){
	        //alert(["编译失败：",buf.join(""),code])
	        throw e;
	    }
	    //this.append("return _$out.join('');");
	},
    findForStatus:function(code){
	    var fis = this.forInfos;
	    var i = fis.length;
	    while(i--){
	        var fi = fis[i];
	        if(fi.code == code){
	            return fi;
	        }
	    }
        //return this.vs.getForStatus(forCode);
    },
    allocateId:function(){
        var i = 0;
        while(true){
            if(!this.idMap[i]){
                this.idMap[i] = true;
                return ID_PREFIX+i.toString(36);
            }
            i++;
        }
    },
    freeId:function(id){
        var i = id.substring(ID_PREFIX.length);
        delete this.idMap[i];
    },
    /**
     */
    appendCode:function(code){
    	for(var i=0;i<code.length;i++){
    		var item = code[i];
    		if(typeof item == 'string'){
    			this.appendOut(stringifyJSON(item))
    		}else{
    			switch(item[0]){
                case EL_TYPE:
                    this.processEL(item);
                    break;
                case XT_TYPE:
                    this.processXMLText(item);
    			    break;
                case XA_TYPE:
                    this.processXMLAttribute(item);
                    break;
                case VAR_TYPE:
                    this.processVar(item);
                    break;
                case CAPTRUE_TYPE:
                    this.processCaptrue(item);
                    break;
                case IF_TYPE:
                    i = this.processIf(code,i);
                    break;
                case FOR_TYPE:
                    i = this.processFor(code,i);
                    break;
    			case PLUGIN_TYPE://not support
    				break;
                //case ELSE_TYPE:
                default:
                    throw Error('无效指令：'+item)
                }
    		}
    	}
    },
    append:function(){
        var depth = this.depth;
        this.out.push("\n");
        while(depth--){
            this.out.push("\t")
        }
        for(var i=0;i<arguments.length;i++){
            this.out.push(arguments[i]);
        }
    },
    appendOut:function(){
    	var len = arguments.length;
    	var last = this.out[this.out.length-1];
    	var data = Array.prototype.join.call(arguments,'');
    	if(last == this.lastOut){
    		data = last.substring(0,last.length-2)+","+data+");";
    		this.out[this.out.length-1] = data;
    	}else{
    		data = "_$out.push("+data+");";
    		this.append(data);
    	}
    	this.lastOut = data
    },
    processEL:function(item){
    	this.appendOut(this.stringifyEL(item[1]))
    },
    processXMLText:function(item){
        this.appendOut("_$replace(",this.stringifyEL(item[1]),")")
    },
    processXMLAttribute:function(item){
        //[7,[[0,"value"]],"attribute"]
        var value = this.stringifyEL(item[1]);
        try{
        	var attributeName = item.length>2?item[2]:null;
        }catch(e){
        	$log.info("@@@@@属性异常："+item.get(2),e)
        }
        if(attributeName){
            var testId = this.allocateId();
            this.append("var ",testId,"=",value);
            this.append("if(",testId,"!=null){");
            this.depth++;
            this.appendOut("' ",attributeName,"=\"',_$replace("+testId+"),'\"'");
            this.depth--;
            this.append("}");
            this.freeId(testId);
        }else{
        	this.appendOut("_$replace(",value,")")
        }
    },
    processVar:function(item){
        this.append("var ",item[2],"=",this.stringifyEL(item[1]),";");
    },
    processCaptrue:function(item){
        var childCode = item[1];
        var varName = item[2];
        var bufbak = this.allocateId();
        this.append("var ",bufbak,"=_$out;_$out=[];");
        this.appendCode(childCode);
        this.append("var ",varName,"=_$out.join('');_$out=",bufbak,";");
        this.freeId(bufbak);
    },
    processIf:function(code,i){
        var item = code[i];
        var childCode = item[1];
        var test = this.stringifyEL(item[2]);
        this.append("if(",test,"){");
        this.depth++;
        this.appendCode(childCode)
        this.depth--;
        this.append("}");
        var nextElse = code[i+1];
        var notEnd = true;
        while(nextElse && nextElse[0] == ELSE_TYPE){
            i++;
            var childCode = nextElse[1];
            var test = this.stringifyEL(nextElse[2]);
            if(test){
                this.append("else if(",test,"){");
            }else{
                notEnd = false;
                this.append("else{");
            }
            this.depth++;
            this.appendCode(childCode)
            this.depth--;
            this.append("}");
            nextElse = code[i+1];
        }
        return i;
    },
    processFor:function(code,i){
        var item = code[i];
        var indexId = this.allocateId();
        var itemsId = this.allocateId();
        var itemsEL = this.stringifyEL(item[2]);
        var varNameId = item[3]; 
        //var statusNameId = item[4]; 
        var childCode = item[1];
        var forInfo = this.findForStatus(item)
        if(forInfo.depth){
            var previousForValueId = this.allocateId();
        }
        //初始化 items 开始
        this.append("var ",itemsId,"=",itemsEL,";");
        this.append("var ",indexId,"=0;")
        this.append(itemsId,"=_$toList(",itemsId,")");
        
        //初始化 for状态
        var needForStatus = forInfo.ref || forInfo.index || forInfo.lastIndex;
        if(needForStatus){
            if(forInfo.depth){
                this.append("var ",previousForValueId ,"=",FOR_STATUS_KEY,";");
            }
            this.append(FOR_STATUS_KEY," = {lastIndex:",itemsId,".length-1};");
        }
        this.append("for(;",indexId,"<",itemsId,".length;",indexId,"++){");
        this.depth++;
        if(needForStatus){
            this.append(FOR_STATUS_KEY,".index=",indexId,";");
        }
        this.append("var ",varNameId,"=",itemsId,"[",indexId,"];");
        this.appendCode(childCode);
        this.depth--;
        this.append("}");
        
        if(needForStatus && forInfo.depth){
           this.append(FOR_STATUS_KEY,"=",previousForValueId);
        }
        this.freeId(itemsId);;
        if(forInfo.depth){
            this.freeId(previousForValueId);
        }
        var nextElse = code[i+1];
        var notEnd = true;
        while(notEnd && nextElse && nextElse[0] == ELSE_TYPE){
            i++;
            var childCode = nextElse[1];
            var test = this.stringifyEL(nextElse[2]);
            if(test){
                this.append("if(!",indexId,"&&",test,"){");
            }else{
                notEnd = false;
                this.append("if(!",indexId,"){");
            }
            this.depth++;
            this.appendCode(childCode)
            this.depth--;
            this.append("}");
            nextElse = code[i+1];
        }
        this.freeId(indexId);
        return i;
    },
    toString:function(){
    	var s = this.out.join('');
    	var p = /\b_\$out.push\((?:(.*)\);)?/g;
    	p.lastIndex=0;
    	p.exec(s);
    	if(!p.exec(s)){
    		s = s.replace(/^\s+var _\$out=\[\];/,'');
    		s = s.replace(p,"return [$1].join('')")
        	return s;
    	}else{
    		return s+ "\n\treturn _$out.join('');\n";
    	}
    }
}