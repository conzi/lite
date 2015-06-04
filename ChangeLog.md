该文档记录Lite自2.0开始的全部更新日志

### 2.0 ###
> 目前2.0尚在开发阶段，当前最新版本[2.0A6](http://code.google.com/p/lite/downloads/list)
  * 2.0A9
    1. Lite**(New)**: 增加html-ext语法支持。
    1. Lite**(New)**: 自动编码功能增加html支持（资源地址自动encodeURIComponent,脚本自动JSON.stringify）。
    1. Lite**(New)**: 增加网站打包支持。
    1. JSEL**(New)**: JSONEncode添加</script的特殊处理，</script 被输出为： <\/script，以确保网页脚本中带</script 的 json数据的输出不打破html语法结构。
  * 2.0A6
    1. Lite**(New)**: 彻底重构前端模板,函数定义能前后端复用且自动整体优化前端代码(前端模板函数能够实现页内共享并能按需生成)
    1. Lite**(New)**: 增加PHP翻译支持, PHP版本可以借助浏览器自动编译.
    1. Lite**(New)**: Java编译器支持XML语法自动修复,能修复大多数常见html类不严谨的书写习惯(目前PHP版本尚无此功能).
  * 2.0A5
    1. JSEL~~(Bug)~~: [JSON 编码工具多线程环境下调用失败](http://code.google.com/p/lite/issues/detail?id=18)
  * 2.0A4
    1. JSEL**(New)**: [支持日期的编码解码，采用W3C日期标准](http://code.google.com/p/lite/issues/detail?id=16)
    1. JSEL~~(Bug)~~: [运算符二级优先级判断错误](http://code.google.com/p/lite/issues/detail?id=17)
    1. JSEL~~(Bug)~~: [表达式数组参数低级错误](http://code.google.com/p/lite/issues/detail?id=15)
  * 2.0A3
    1. JSEL**(New)**: [JSON编码/解析实现优化](http://code.google.com/p/lite/wiki/JSON)
    1. JSEL**(New)**: [命令行工具优化](http://code.google.com/p/lite/wiki/CPEL)
  * 2.0A2
    1. JSEL**(New)**: [表达式扩展支持](http://code.google.com/p/lite/wiki/JSELExtension)
  * 2.0A1
    1. Lite**(New)**: 优化插件模型, 用javascript重构核心逻辑，方便前端工程师对语法设计的控制。