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
		<#include "/fragments/authclientheader.ftl">
		<div class="panel-body" style = "margin: 0px">
			<div class = "alert alert-warning">
				After accepting ${pledge}% from ${cost} = ${cost*pledge/100} will be withdrawn from your 
				balance, and translator will be notified about beginning translation.
			</div>
			<form action = "<@spring.url "/client/accept"/>" method = "Post" role = "form">
				<input name="id" type="hidden" value="${id}"/>
				<button type = "submit" class="btn btn-info">
					Accept
				</button>
				<input type="hidden"
						name="${_csrf.parameterName}"
						value="${_csrf.token}"/>
			</form>
			<a href="<@spring.url "/client/responses"/>">Cancel</a>
		</div>
	</div>
	</div>
</body>
</html>