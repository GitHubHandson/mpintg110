<%@page import="com.hybris.integration.util.ChineseUtil"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.util.Enumeration"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");

	String path = request.getContextPath();

	Enumeration e = request.getParameterNames();
	while (e.hasMoreElements()) {
		String paramKey = (String) e.nextElement();
		String paramValue = request.getParameter(paramKey);
		
		if (StringUtils.isNotEmpty(paramValue)) {
			if(ChineseUtil.isMessyCode(paramValue)){
				paramValue = new String(paramValue.getBytes("ISO-8859-1"),"UTF-8");
			}
			pageContext.setAttribute(paramKey, paramValue);
		}
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=640,target-densitydpi=320,user-scalable=no">
<meta name="format-detection" content="telephone=no" />
<meta HTTP-EQUIV="pragma" CONTENT="no-cache">
<meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate">
<meta HTTP-EQUIV="expires" CONTENT="0">
<title>oauth</title>
<link href="<%=path%>/css/auth.css" rel="stylesheet" />
<script type="text/javascript">
	function CloseWebPage() {
		if (navigator.userAgent.indexOf("MSIE") > 0) {
			if (navigator.userAgent.indexOf("MSIE 6.0") > 0) {
				window.opener = null;
				window.close();
			} else {
				window.open('', '_top');
				window.top.close();
			}
		} else if (navigator.userAgent.indexOf("Firefox") > 0) {
			window.location.href = 'about:blank ';
		} else {
			window.opener = null;
			window.open('', '_self', '');
			window.close();
		}
	}
	function SubmitForm() {
		var appkey = document.getElementById("secret").value;
		var secret = document.getElementById("appkey").value;
		if (appkey == "" || secret == "") {
			alert("请把信息填写完整.");
			return;
		}

		document.getElementById("authForm").submit();
	}
</script>
</head>

<body>
	<div class="header">
		<img src="<%=path%>/images/logo.png" title="Logo" />
	</div>
	<div class="body">
		<div>
			<span style="font-size: 24px;">淘宝登录授权</span>
			<form id="authForm" action="<%=path%>/view/accesstoken/toOauth?timestamp=<%=System.currentTimeMillis() %>"
				method="POST">
				<ul>
					<c:if test="${not empty error}">
						<li><font color="red">appkey或者secret不合法，请检查.</font></li>
					</c:if>
					<li>
						<b>AppKey:</b> 
						<input type="hidden" name="marketplaceStoreId" value="${marketplaceStoreId}" /> 
						<input type="text" placeholder="AppKey" id="appkey" onkeyup="value=value.replace(/[^\w\.\/]/ig,'')" name="appkey" maxlength="8" />
					</li>
					<li>
						<b>Secret:</b> 
						<input type="text" placeholder="Secret" id="secret" onkeyup="value=value.replace(/[^\w\.\/]/ig,'')" name="secret" maxlength="32" />
					</li>
					<li>
						<input type="button" class="button" onclick="SubmitForm();" value="授权" style="margin-right: 15px;" />
						<input type="button" class="button" onclick="CloseWebPage();" value="关闭" />
					</li>
				</ul>
			</form>
		</div>
	</div>
</body>

</html>