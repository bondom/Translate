<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring/>
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<html>
<head>
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/css/bootstrap.min.css"/>"/> 
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Admin page</title>
</head>
<body>
	
	<div class="container">
		<div class="panel panel-default">
			<#include "/fragments/authadminheader.ftl">
			<div class="panel-body" style = "margin: 0px">
			It is page for admin
			
			</div>
		</div>
	</div>

</body>
</html>