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
<title>图片搜索</title>
<style>
body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,menu,figure,pre,code,form,fieldset,legend,input,button,textarea,p,blockquote,th,td{ padding:0; margin:0;}
.header {
    height: 40px;
    position: fixed;
    top: 0;
    min-width: 320px;
    max-width: 640px;
    width: 100%;
    z-index: 500;
    background: #fafafa;
}
.header .title {
    margin: 0px 40px 0px 40px;
    height: 40px;
    line-height: 40px;
    font-size: 16px;
    text-align: center;
}
</style>
</head>
<body>
	<div style="min-width: 320px;max-width: 640px;">
		<div class="header">
		    <div class="title">图片搜索</div>
		</div>
		<div style="width:100%;text-align:center;margin-top:200px;">
	    <form action="imageSearch" id="imageSearch" method="post" enctype="multipart/form-data">
		    <input type="file" name="upfile" style="display:none;"/> 
		    <br />
		    <span onclick="$('input[name=upfile]').click();" style="padding:8px 50px 8px 50px;border: 1px solid #5eaffe;">上传图片</span>
		</form>
		</div>
	</div>
	<script type="text/javascript" src="resources/js/jquery-1.11.1.min.js"></script>
	<script>
	$("input[name=upfile]").bind("change",function () {
        $("#imageSearch").submit();
    });
	</script>
</body>
</html>