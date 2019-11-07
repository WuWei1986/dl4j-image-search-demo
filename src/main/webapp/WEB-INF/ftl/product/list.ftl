<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0" />
<meta name="apple-mobile-web-app-capable" content="yes"/>
<meta name="apple-mobile-web-app-status-bar-style" content="black"/>
<meta name="wap-font-scale" content="no"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="Expires" content="0"/> 
<meta http-equiv="Cache-Control" content="no-cache"/> 
<meta http-equiv="Pragma" content="no-cache"/>
<title>搜索列表</title>
<style>
body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,menu,figure,pre,code,form,fieldset,legend,input,button,textarea,p,blockquote,th,td{ padding:0; margin:0;}
ol,ul,li{list-style:none;}
a,a:hover{text-decoration:none; color:#333;}
img{border:0px}
input[type=submit],input[type=button],input[type=text],input[type=password]{ -webkit-appearance:none; box-sizing:content-box;border:0; border-radius:0;box-shadow: none;outline: none;}
body{font-family:"华文细黑","Microsoft YaHei","黑体",sans-serif;font-size: 14px;line-height: 1.5;outline: none;color:#666;}
.clearfix:after{clear:both;display: block; content: "..."; visibility: hidden; height: 0; font-size: 0;}
.clearfix{*zoom:1;}
.clear{ clear:both;}
li{float:left;width:50%;height:auto;padding:20px 5px 10px;position:relative;background:#fff;margin-top:3px;border-left:2px solid #f3f5f6;border-right:1px solid #f3f5f6;-webkit-box-sizing: border-box;}
.header {
    height: 40px;
    background: #fff;
    position: fixed;
    top: 0;
    min-width: 320px;
    max-width: 640px;
    width: 100%;
    z-index: 500;
}
.header .title {
    margin: 0px 40px 0px 40px;
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    text-align: center;
}
.header .back {
    margin-left:15px;
    position: absolute;
    cursor: pointer;
    line-height: 40px;
    text-align:center;
    font-size:20px;
}
body{background: #f3f5f6;}
</style>
</head>
<body>
	<div style="min-width: 320px;max-width: 640px;">
		<div class="header">
		    <a href="javascript:void(0)" onclick="history.back();">
		    <div class="back">&lt;</div>
		    </a>
		    <div class="title">搜索列表</div>
		</div>
		<div style="width:100%;margin-top:40px;">
	    	<ul class="clearfix">
	    		<#if productList?? && productList?size gt 0>
	    		<#list productList as item>
	    		<li class="clearfix">
	    			<a href="javascript:;"><img src="${item.imgUrl}" style="margin: 0 auto;float:none;width:110px;height:110px;display:block;"/></a>
	    		</li>
	    		</#list>
	    		</#if>
	    	</ul>
		</div>
	</div>
</body>
</html>