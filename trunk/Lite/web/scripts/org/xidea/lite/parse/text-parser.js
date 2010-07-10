/*
 * List Template
 * License LGPL(您可以在任何地方免费使用,但请不要吝啬您对框架本身的改进)
 * http://www.xidea.org/project/lite/
 * @author jindw
 * @version $Id: template.js,v 1.4 2008/02/28 14:39:06 jindw Exp $
 */

function parseText(text,context,chain,textParsers){
	switch(typeof text == 'string' && context.textType){
    case XML_ATTRIBUTE_TYPE :
        var escapeQute = '"';
    case XML_TEXT_TYPE :
        var encode = true;  
        break;
    case EL_TYPE :
        var textType = EL_TYPE;
    default:
    	chain.next(text);
    	return;
    }
	var len = text.length;
	var start = 0;
	do {
		var nip = null;
		var p$ = len + 1;
		{
			var pri = 0;
			var ti = textParsers.length;
			while (ti--) {
				var ip = textParsers[ti];
				var p$2 = ip.findStart(text, start, p$);
				var pri2 = ip.priority || 1;
				if (p$2 >= start ){
					if(p$2 < p$ || p$2 == p$ && pri2>pri){
						p$ = p$2;
						nip = ip;
						pri = pri2;
					}
				}
				
			}
		}
		if (nip != null) {
			var escapeCount = countEescape(text, p$);
			context.append(text
					.substring(start, p$ - (escapeCount + 1) / 2), encode,
					qute);
			if ((escapeCount & 1) == 1) {// escapsed
				start = nextPosition(context, text, p$);
			} else {
				start = p$;
				var mark = context.mark();
				try {
					start = nip.parseText(text, start, context);
				} catch (e) {
					$log.warn("尝试表达式解析失败:[fileName:"+context.currentURI+"]",text,e);
				}
				if (start <= p$) {
					context.reset(mark);
					start = nextPosition(context, text, p$);
				}

			}
		} else {
			break;
		}
	} while (start < len);
	if (start < len) {
		context.append(text.substring(start), encode, qute);
	}
}
function nextPosition(context, text, p$) {
	context.append(text.substring(p$, p$ + 1));
	return p$ + 1;
}

function countEescape(text, p$) {
	if (p$ > 0 && text.charAt(p$ - 1) == '\\') {
		var pre = p$ - 1;
		while (pre-- > 0 && text.charAt(pre) == '\\')
			;
		return p$ - pre - 1;
	}
	return 0;
}
