<?php
require_once("WEB-INF/classes/lite/LiteEngine.php");
// 通过上下文数据方式传递模板参数：
$engine = new LiteEngine();
$context = array("int1"=>1,"text1"=>'1');
$engine->render("/test.xhtml",$context);