<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring>
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
			<#if error??>
				<h4>You have already confirmed your email.</h4>
				<#else>
				<h4>Letter for confirmation email is sended to your email <br>
					<@security.authentication property = "principal.username"/>
				</h4>
				<p>After 2-3 minutes letter should be delivered to your email. Please, open letter
				and go to link for confirmation. After confirmation, all responses on your advertisements
				will be sended to your email.
			</#if>
			</div>
		</div>
	</div>
</body>
</html>