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
	<div class="panel-heading">
		<a href = "<@spring.url "/translator/index"/>" class="btn btn-info" role="button">LANGUAGES.RU</a>
		<a href = "<@spring.url "/orders"/>" class="btn btn-info" role="button">Find orders</a>
		<a href = "<@spring.url "/translator/login"/>" class="btn btn-info" role="button">Log In</a>
		<a href = "<@spring.url "/index"/>"  role="button">To Client</a>
	</div>

</body>
</html>