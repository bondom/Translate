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
		<#if user??>
			<img  src="data:image/jpeg;base64,${image!""}" />
			<table>
			<tr><td>Name: </td><td>${user.getName()}</td></tr>
			<tr><td>Surname: </td><td>${user.getSurname()}</td></tr>
			<tr><td>Login: </td><td>${user.getLogin()}</td></tr>
			<tr><td>Email: </td><td>${user.getEmail()}</td></tr>
			<#if user.phoneNumber?has_content>
			<tr><td>Phone Number: </td><td>${user.getPhoneNumber()}</td></tr>
			</#if>
			</table>
			<a href = "<@spring.url "/personal/edit"/>">Edit profile</a>
		</#if>
	</div>
</body>
</html>