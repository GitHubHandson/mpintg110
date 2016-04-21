<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
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
<title>Oauth result</title>
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
</script>
</head>
<body>
	<c:choose>
		<c:when test="${not empty error}">
			<div class="header">
				<img src="<%=path%>/images/logo.png" title="Logo" />
			</div>
			<div class="body">
				<div>
					<span style="font-size: 24px;">Oauth result</span>
					<ul>
						<li>发生错误：${error }</li>
						<li><input type="button" class="button"
							onclick="CloseWebPage();" value="关闭" /></li>
					</ul>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<script type="text/javascript">
				alert("授权绑定成功！");
				CloseWebPage();
			</script>
		</c:otherwise>
	</c:choose>
</body>
</html>