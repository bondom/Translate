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
		<#include "/fragments/authheader.ftl">
		<div class="panel-body" style = "margin: 0px">
			<#if client??>
				<img  src="data:image/jpeg;base64,${image!""}" />
				<table>
				<tr><td>First Name: </td><td>${client.getFirstName()}</td></tr>
				<tr><td>Last Name: </td><td>${client.getLastName()}</td></tr>
				<tr><td>Country: </td><td>${client.getCountry()}</td></tr>
				<tr><td>City: </td><td>${client.getCity()}</td></tr>
				<tr><td>Phone Number: </td><td>${client.getPhoneNumber()}</td></tr>
				<tr><td>Email: </td><td>${client.getEmail()}</td></tr>
				<tr><td>Registration Time: </td><td>${client.getRegistrationTime()}</td></tr>
				</table>
				<a href = "<@spring.url "/client/edit"/>">Edit profile</a>
			</#if>
			<@security.authorize access="isRememberMe()">
				<p>O yeep!
			</@security.authorize>
		</div>
	</div>
	</div>
</body>
</html>