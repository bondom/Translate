<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring>
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<html>
<head>
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/css/bootstrap.min.css"/>"/>         
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<div class="container">
	<div class="panel panel-default">
		<@security.authorize access="hasRole('ROLE_CLIENT') or ! isAuthenticated()">
			<#include "/fragments/inittranslatorheader.ftl">
		</@security.authorize>
		<@security.authorize access="hasRole('ROLE_TRANSLATOR')">
			<#include "/fragments/authtranslatorheader.ftl">
		</@security.authorize>
		<div class="panel-body" style = "margin: 0px">
			Advertisement on adress ${errorUrl} doesn't exist or is executed</br>
		</div>
	</div>
	</div>
</body>
</html>