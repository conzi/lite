/*
 * List Template
 * License LGPL(您可以在任何地方免费使用,但请不要吝啬您对框架本身的改进)
 * http://www.xidea.org/project/lite/
 * @author jindw
 * @version $Id: template.js,v 1.4 2008/02/28 14:39:06 jindw Exp $
 */


function parseDefaultXMLNode(node,context,chain){
    switch(node.nodeType){
        case 1: //NODE_ELEMENT 
            processElement(node,context,chain)
            break;
        case 2: //NODE_ATTRIBUTE                             
            processAttribute(node,context,chain)
            break;
        case 3: //NODE_TEXT                                        
            processTextNode(node,context,chain)
            break;
        case 4: //NODE_CDATA_SECTION                     
            processCDATA(node,context,chain)
            break;
        case 5: //NODE_ENTITY_REFERENCE                
            processEntityReference(node,context,chain)
            break;
        case 6: //NODE_ENTITY            
            processEntity(node,context,chain)
            break;
        case 7: //NODE_PROCESSING_INSTRUCTION    
            processProcessingInstruction(node,context,chain)
            break;
        case 8: //NODE_COMMENT                                 
            processComment(node,context,chain)
            break;
        case 9: //NODE_DOCUMENT                                
        case 11://NODE_DOCUMENT_FRAGMENT             
            processDocument(node,context,chain)
            break;
        case 10://NODE_DOCUMENT_TYPE                     
            processDocumentType(node,context,chain)
//        case 11://NODE_DOCUMENT_FRAGMENT             
//            processDocumentFragment(node,context,chain)
            break;
        case 12://NODE_NOTATION 
            processNotation(node,context,chain);
            break;
        default://文本节点
        	chain.next(node);
            //this.println("<!-- ERROR： UNKNOW nodeType:"+node.nodeType+"-->")
    }
}

var htmlLeaf = /^(?:meta|link|img|br|hr|input)$/i;
var scriptTag = /^script$/i
function processElement(node,context,chain){
    var attributes = node.attributes;
    context.append('<'+node.tagName);
    for (var i=0; i<attributes.length; i++) {
        try{
            //htmlunit bug...
            var attr = attributes.item(i);
        }catch(e){
            var attr =attributes[i];
        }
        context.parse(attr)
    }
    if(htmlLeaf.test(node.tagName)){
        context.append('/>')
        return ;
    }
    context.append('>')
    var child = node.firstChild
    if(child){
        do{
            context.parse(child)
        }while(child = child.nextSibling)
    }
    context.append('</'+node.tagName+'>')
}

//parser attribute
function processAttribute(node,context,chain){
    var name = String(node.name);
    var value = String(node.value);
    var buf = context.parseText(value,XML_ATTRIBUTE_TYPE);
    var isStatic;
    var isDynamic;
    //hack context.parseText is void 
    var i =  buf.length;
    while(i--){
        //hack reuse value param
        var value = buf[i];
        if(value.constructor == String){
            if(value){
                isStatic = true;
            }else{
                buf.splice(i,1);
            }
        }else{
            isDynamic = true;
        }
    }
    if(isDynamic && !isStatic){
        //remove attribute;
        //context.append(" "+name+'=""');
        if(buf.length > 1){
            //TODO:....
            throw new Error("属性内只能有单一EL表达式！！");
        }else{//只考虑单一EL表达式的情况
            buf = buf[0];
            //buf[1] 是一个表达式对象
	        context.appendXA(name,buf[1]);
	        return null;
        }
    }
    context.append(" "+name+'="');
    if(/^xmlns$/i.test(name)){
        if(buf[0] == 'http://www.xidea.org/ns/lite/xhtml'){
            buf[0] = 'http://www.w3.org/1999/xhtml'
        }
    }
    context.appendAll(buf);
    context.append('"');
}
function processTextNode(node,context,chain){
    var data = String(node.data);
    //context.appendAll(context.parseText(data.replace(/^\s*([\r\n])\s*|\s*([\r\n])\s*$|^(\s)+|(\s)+$/g,"$1$2$3$4"),XML_TEXT_TYPE))
    //不用回车js序列化后更短
    data = data.replace(/^\s*([\r\n])\s*|\s*([\r\n])\s*$|^(\s)+|(\s)+$/g,"$1$2$3$4");
    context.appendAll(context.parseText(data,XML_TEXT_TYPE))
}

function processCDATA(node,context,chain){
    context.append("<![CDATA[");
    context.appendAll(context.parseText(node.data,EL_TYPE));
    context.append("]]>");
}
function processEntityReference(){
    return null;//not support
}
function processEntity(){
    return null;//not support
}
function processProcessingInstruction(node,context,chain){
    context.append("<?"+node.nodeName+" "+node.data+"?>");
}
function processComment(){
    return null;//not support
}
function processDocument(node,context,chain){
    for(var n = node.firstChild;n!=null;n = n.nextSibling){
        context.parse(n);
    }
}
///**
// * @protected
// */
//function processDocumentFragment(node,context,chain){
//    var nl = node.childNodes;
//    for (var i=0; i<nl.length; i++) {
//        context.parse(nl.item(i));
//    }
//}
/**
 * @protected
 */
function processDocumentType(node,context,chain){
    if(node.xml){
        context.append(node.xml);
    }else{
    	var pubid = node.publicId;
    	var nodeName = node.nodeName;
		var sysid = node.systemId;
        if(pubid){
			if(pubid == "org.xidea.lite.OUTPUT_DTD"){
				if(sysid){
					context.append(decodeURIComponent(sysid));
				}
				return;
			}
            context.append('<!DOCTYPE ');
            context.append(nodeName);
            context.append(' PUBLIC "');
            context.append(pubid);
            if (sysid == null) {
            	context.append( '" "');
            	context.append(sysid);
            }
            context.append('">');
        }else if(sysid){
            context.append('<!DOCTYPE ');
            context.append();
            context.append(' SYSTEM "');
            context.append(sysid);
            context.append('">');
        }else{
        	context.append("<!DOCTYPE ");
			context.append(node.getNodeName());
			var sub = node.internalSubset;
            if(sub){
				context.append(" [");
				context.append(sub);
				context.append("]");
			}
			context.append(">");
        }
    }
}

/**
 */
function processNotation(node,context,chain){
    return null;//not support
}

//1 2


