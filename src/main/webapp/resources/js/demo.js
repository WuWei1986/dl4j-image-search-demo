var mobileRegex = /^1[3-9][0-9]{9}$/;

// 获取cookie
function getCookie(cookieName) {
	var arrStr = document.cookie.split("; ");
	for (var i = 0; i < arrStr.length; i++) {
		var temp = arrStr[i].split("=");
		if (temp[0] == cookieName) {
			return unescape(temp[1])
		}
	}
	return ""
}

// 设置cookie
function setCookie(cookieName, value, expiredays) {
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + expiredays);
	document.cookie = cookieName + "=" + escape(value)
			+ ((expiredays == null) ? "" : ";expires=" + exdate.toGMTString())
			+ ";path=/;domain=domain.com"
}

// ajax操作
// http://www.w3school.com.cn/jquery/ajax_ajax.asp
var htmlobj=$.ajax({url:"/jquery/test1.txt",async:false});
$("#myDiv").html(htmlobj.responseText);

// http://www.w3school.com.cn/jquery/ajax_get.asp
$.ajax({
	url : url,
	data : data,
	success : success,
	dataType : dataType
});

// http://www.w3school.com.cn/jquery/ajax_post.asp
$.ajax({
  type: 'POST',
  url: url,
  data: data,
  success: success,
  dataType: dataType
});

// http://www.w3school.com.cn/jquery/jquery_ajax_get_post.asp
$.get(URL,callback);
$.post(URL,data,callback);

// http://www.w3school.com.cn/jquery/jquery_ajax_load.asp
$("#myDiv").load(url, function(responseTxt, statusTxt, xhr) {
	if (statusTxt == "success")
		alert("外部内容加载成功！");
	if (statusTxt == "error")
		alert("Error: " + xhr.status + ": " + xhr.statusText);
});

// jsonp
jQuery.ajax({
	type:'POST',
	url : url,
	data:{},
	dataType : "jsonp",
	jsonp : "callback",
	success : function(data) {
	},
	error:function(){
	}
});

// 是否ios客户端
function isIOS(u){
	return !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
}