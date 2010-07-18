/*
 *          foo://example.com:8042/over/there?name=ferret#nose
 *          \_/   \______________/\_________/ \_________/ \__/
 *           |           |            |            |        |
 *        scheme     authority       path        query   fragment
 *           |   _____________________|__
 *          / \ /                        \
 *          urn:example:animal:ferret:nose
 */
var uriPattern = /^([a-zA-Z][\w\.]*)\:(?:(\/\/[^\/]*))?(\/?[^?#]*)(\?[^#]*)?(#[\s\S]*)?$/;
var absURIPattern = /^[a-zA-Z][\w\.]*\:/;
var uriChars = /\\|[\x22\x3c\x3e\x5c\x5e\x60\u1680\u180e\u202f\u205f\u3000]|[\x00-\x20]|[\x7b-\x7d]|[\x7f-\xa0]|[\u2000-\u200b]|[\u2028-\u2029]/g;
var allEncodes = /[\x2f\x60]|[\x00-\x29]|[\x2b-\x2c]|[\x3a-\x40]|[\x5b-\x5e]|[\x7b-\uffff]/g;
///[\x22\x25\x3c\x3e\x5c\x5e\x60\u1680\u180e\u202f\u205f\u3000]|[\x00-\x20]|[\x7b-\x7d]|[\x7f-\xa0]|[\u2000-\u200b]|[\u2028-\u2029]/g;

var encodeURIComponent = window.encodeURIComponent;
var decodeURIComponent = window.decodeURIComponent;
encodeURIComponent = encodeURIComponent || function(url){
	String(url).replace(/(?:%[\da-fA-F]{2})+/g,decodeChar);
}
decodeURIComponent = decodeURIComponent || function(url){
	String(url).replace(allEncodes,uriDecode);
}
function encodeChar(i){
	return "%"+(0x100+i).toString(16).substring(1)
}
function decodeChar(c){
	var n = c.charCodeAt();
    if (n < 0x80){
        return encodeChar(n);
    }else if (n < 0x800){
    	return encodeChar(0xc0 | (n >>>  6))+encodeChar(0x80 | (n & 0x3f))
    }else{
    	return encodeChar( 0xe0 | ((n >>> 12) & 0x0f))+
    		encodeChar(0x80 | ((n >>>  6) & 0x3f))+
    		encodeChar(0x80 | (n & 0x3f))
    }
}
function uriDecode(source){
	//192,224,240
	for(var result = [], i=1;i<source.length;i+=3){
		var c = parseInt(source.substr(i,2),16);
		if(c>=240){//其实无效，js无法处理超出2字节的字符
			c = (c & 0x07)<<18;
			c += (parseInt(source.substr(i+=3,2),16) &0x3f)<<12;
			c += (parseInt(source.substr(i+=3,2),16) &0x3f)<<6;
			c += (parseInt(source.substr(i+=3,2),16) &0x3f);
		}else if(c>=224){
			c = (c & 0x0f)<<12;
			c += (parseInt(source.substr(i+=3,2),16) &0x3f)<<6;
			c += (parseInt(source.substr(i+=3,2),16) &0x3f);
		}else if(c>=192){
			c = (c & 0x1f)<<6;
			c += (parseInt(source.substr(i+=3,2),16) &0x3f);
		}
		result.push(String.fromCharCode(c))
	}
	return result.join('');
}
function uriReplace(c){
	if(c == '\\'){
		return '/';
	}else{
		return decodeChar(c);
	}
}
function URI(path){
	if(path instanceof URI){
		return path;
	}
	path = String(path).replace(uriChars,uriReplace)
	if(/^%3c|^</i.test(path)){
		return new URI("data:text/xml,"+path);
    }
    //normalize
	path = path.replace(/\/\.\//g,'/');
	while(path != (path = path.replace(/[^\/]+\/\.\.\//g,'')));
	var match = path.match(uriPattern);
	if(match){
		setupURI(this,match);
	}else{
		$log.error("url must be absolute,"+path)
	}

}

function setupURI(uri,match){
	uri.value = match[0];
	uri.scheme = match[1];
	uri.authority = match[2];
	uri.path = match[3];
	uri.query = match[4];
	uri.fragment = match[5];
	
	 
	if('data' == uri.scheme){
		match = uri.value
		uri.source = decodeURIComponent(match.substring(match.indexOf(',')+1));
		
	}
}
URI.prototype = {
	resolve:function(path){
		path = String(path);
		if(path.charAt() == '<' ||absURIPattern.test(path)){
			path = new URI(path);
			return path;
		}
		path = path.replace(uriChars,uriReplace)
		if(path.charAt() != '/'){
			var p = this.path;
			path = p.replace(/[^\/]*$/,path);
		}
		return new URI(this.scheme + ':'+(this.authority||'') + path);
	},
	toString:function(){
		return this.value;
	}
}