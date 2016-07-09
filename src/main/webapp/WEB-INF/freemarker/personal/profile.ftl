<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring>
<html>
<head>
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/css/bootstrap.min.css"/>"/>         
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<#include "/fragments/authheader.ftl">
	<div class="panel-body" style = "margin: 0px">
		<a href = "<@spring.url "/personal/profileInfo"/>">Info</a>
		<a href = "<@spring.url "/personal/settings"/>">Settings</a>
		<#if authenticatedUser??>
			<ul>
			<li>${authenticatedUser.getLogin()}</li>
			<li>${authenticatedUser.getEmail()}</li>
			<li>${authenticatedUser.getId()}</li>
			</ul>
			<img  src="data:image/jpeg;base64,${image}" />
		</#if>
	</div>
</body>
</html>