### Lite 是什么 ###

缩写自List Template，是一个由简单的控制指令集和一个表达式解析引擎组成的简单模板引擎。
模板语言所解析的中间格式，由数组、字符串、和整数三种数据类型组成。数组就是这里唯一的复合类型，于是，我采用List Template来命名这个模板引擎。List 和Template各取两个首字母，组成Lite这个单词。

用户不能直接编写控制指令，如同java程序员不能直接编写字节码一样，Lite需要一种真正的源代码格式。理论上，通过这些控制指令和自定义表达式函数，我们可以支持任何模板语法翻译为Lite能解释的中间代码。

如CLR需要C#，C++.net，java byte code需要Java语法;Lite提供一种XML源代码语法，作为Lite的默认源代码格式。

### Lite 支持那些环境 ###
  * Java
> 支持模板编译和解释,采用JSEL作为模板的默认表达式实现（类似js语法，支持全部JS全局函数）。在编译支持方面，除了提供本地的动态编译之外，还提供一种供远程调用的编译服务。为 php，python等没有内置编译模板的模板实现提供自动编译服务。
  * PHP
> 支持模板浏览器自动编译和调试服务器自动编译两种工作模式；上线之后，建议统一批量编译，2.0开始，语法上与Java/JS版本完全一致（包括表达式语法，内置函数），PHP版本采用编译成最终PHP代码的方式运行，性能高于Smarty3 1.5倍左右。
  * JS
> 支持模板编译和解释。虽然js端也支持模板的编译，但是不建议将其在本地执行。
> 推荐的使用方法有：
    1. 在任何一种Lite服务端模板中插入客户端模板区域。在服务端模板编译过程中，顺带编译客户端模板。
    1. 使用JSI的导出程序，在脚本的开发期间完成模板的编译优化。





------------------------ 以下实现尚在试验阶段 ---------------------------

  * _Python_
  1. 0 有过支持，2.0目前还没有跟进，支持模板解释，开发期间可自动调用Java 远程编译服务，实现自动编译；上线之后，建议统一批量编译。
  * _C/C++支持_
> _共享了编译的工作，直接解释执行中间代码是一件比较愉快的事情，不过目前还没有成熟的运行环境支持_
  * _其他支持_
> _目前暂无，如果您希望在其他语言中运用lite，需要编写其他语言的运行时，欢迎与我们联系，我们可以提供相关技术文档和规范的支持。_

因为Lite本身的简单性，如果，我们仅仅支持其运行环境，是一件非常简单的事情。所以以后对其他脚本的运行环境支持也很快将得到支持。

### Lite效率 ###
Lite的运行效率，是非常出众的，高于Velocity和FreeMarker一半以上。
但是对于一些表达式的计算，因为JSEL脚本和JavaScript规则的兼容性要求，以及弱类型自动转换的特征，性能不及强类型的velocity。介于Velocity和FreeMarker之间。

js版本运行时比jst快大约两倍，如果算上编译时间（lite不在运行时编译），不在一个数量级上，没有比较。

python版本比django模板快四倍左右!

1.0 时代，php版本的性能较差，在2.0之后，采用类似lite4js那样编译成最终代码的方式，运行效率大幅提升，一般情况下，是Smarty3的1.5倍左右。

几个Java模板引擎的测试结果比较见：http://code.google.com/p/templatetest/wiki/Velocity_CommonTemplate_XMLTemplate_Compare

PHP 模板引擎详细测试数据见：[待续]

### Lite 的XML语法简介 ###

Lite的语法非常简单，如果你熟悉jstl，那么，你基本也就熟悉了Lite XML
  * [表达式输出](http://www.xidea.org/lite/doc/index.php/guide/syntax-out.xhtml)
  * [if/else指令](http://www.xidea.org/lite/doc/index.php/guide/syntax-if.xhtml)
  * [for指令](http://www.xidea.org/lite/doc/index.php/guide/syntax-for.xhtml)
  * [var指令](http://www.xidea.org/lite/doc/index.php/guide/syntax-var.xhtml)
  * [客户端模板](http://www.xidea.org/lite/doc/index.php/guide/syntax-client.xhtml)（编译期将特定模板片断翻译成javascript实现函数）
  * [extends指令](http://www.xidea.org/lite/doc/index.php/guide/syntax-extends.xhtml)（模板继承）
  * [自动表单填充](http://www.xidea.org/lite/doc/index.php/guide/syntax-autoform.xhtml)（取代 JSP From Tag）

更多介绍见：
[LiteXML](http://www.xidea.org/lite/doc/index.php/guide/index.xhtml)。
文档中出现的示例，可以在线测试。

### Lite 的XML语法的优势 ###

XML良好的结构，对于模板的语意表达和代码组织非常有利。

同时，模板通常用来生成类XML数据（如html），XML源代码懂XML语法，用它来生成标记另外一种类XML标记语言，有一定的先天优势。

主要表现有：
  * 代码复用更加方便
  * 自动化的编码处理和语法优化。
  * 保障输出结构的合法性，减少XSS漏洞的发生。
  * 更好的工具支持和编码效率。
更多介绍见：
[LiteSyntacticSugar](http://code.google.com/p/lite/wiki/LiteSyntacticSugar)