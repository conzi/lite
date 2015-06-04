### 默认类型转换支持 ###
命令行解析过程中，输入永远只能是字符串，而且不能是空字符串，也不能为null，类型转换的功能就是吧这些输入字符串转化为需要的特定类型。

  * 类型转换
    1. 数值类型(Long,Integer,Double,Short,Byte,long,int,double,short,byte)
      1. 调用相关的解析函数（如：Double.parseDouble(value)）
    1. 布尔类型(Boolean，boolean)
      1. 对于"true"，"false"，返回相关boolean 值true,false
      1. 打印警告日志（非标准boolean字面量）
      1. 对于"","0","0.0","FALSE"（大小写不敏感）为false，其他值均为true
    1. 字符串（String）
> > > 不需要转换
    1. 字符（char,Character）
> > > 取第一个字符
    1. Object
> > > 当String返回，不转换
    1. 资源对象(File, URL, URI)
> > > 调用他的字符串参数构造器:
```
...
new File(String path);
...
new URL(String path);
...
new URI(String path);
```