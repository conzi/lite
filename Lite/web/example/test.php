<?php
require_once(dirname(__FILE__)."/../WEB-INF/classes/lite/LiteEngine.php");
$engine = new LiteEngine();
# ͨ�����������ݷ�ʽ����ģ�������
$context = array(
	"int1"=>1,
	"text1"=>'1'
);
$engine->render("/example/extends-page.xhtml",$context);
?>