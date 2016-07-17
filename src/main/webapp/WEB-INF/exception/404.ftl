<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring/>
<#assign  security=JspTaglibs["http://www.springframework.org/security/tags"] />
<html>
<head>
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/css/bootstrap.min.css"/>"/>         
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>404</title>
</head>
<body>
	<div class="container">
	  	<div class="panel panel-default">
			<@security.authorize access="hasRole('ROLE_CLIENT')">
				<#include "/fragments/authclientheader.ftl">
			</@security.authorize>
			<@security.authorize access = "hasRole('ROLE_TRANSLATOR')">
				<#include "/fragments/authtranslatorheader.ftl">
			</@security.authorize>
			<@security.authorize access = "! isAuthenticated()">
				<#include "/fragments/initclientheader.ftl">
			</@security.authorize> 
			<div class="panel-body" style = "margin: 0px">
				<h3>PAGE NOT FOUND</h3>
				<p>Perhaps, incorrectly typed the address of a page or no longer exists.
			</div>
		</div>
	</div>
	
	

</body>
</html>