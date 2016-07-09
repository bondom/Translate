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
		<kbd>
			<@security.authentication property = "principal.username"/>
		</kbd>
		<a href = "<@spring.url "/personal/profile"/>" class="btn btn-info" role="button">My Profile</a>
		<a href = "<@spring.url "/personal/search"/>" class="btn btn-info" role="button">Search</a>
		<a href = "<@spring.url "/logout"/>" class="btn btn-info" role="button">Log out</a>
	</div>

</body>
</html>