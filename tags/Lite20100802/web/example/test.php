<?php
require_once("../WEB-INF/classes/lite/TemplateEngine.php");
$engine = new TemplateEngine();
# ͨ�����������ݷ�ʽ����ģ�������
$context = array(
	"int1"=>1,
	"text1"=>'1'
);
$engine->render("/example/test.xhtml",$context);
?>