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
			
			<#if error??>
				<div class="alert alert-danger">${error}</div>
			</#if>
			<#if emailSaved??>
				<div class="alert alert-success">${emailSaved}</div>
			</#if>
			<#if passSaved??>
				<div class="alert alert-success">${passSaved}</div>
			</#if>
			<#if profileSaved??>
				<div class="alert alert-success">${profileSaved}</div>
			</#if>
			<img  src="data:image/jpeg;base64,${image!""}" />
			Email: ${client.getEmail()} <a href="<@spring.url "/client/email"/>">Edit</a>
			<#if client.getEmailStatus().name()=="NOTCONFIRMED">
				<div class="alert alert-warning">
						You have not confirmed your email.
				</div>
				<a href="<@spring.url "/client/email-confirm"/>">Confirm</a></br>
			</#if>
			</br>Password:***** <a href="<@spring.url "/client/password"/>">Edit</a></br>
			<table>
			<tr><td>First Name: </td><td>${client.getFirstName()}</td></tr>
			<tr><td>Last Name: </td><td>${client.getLastName()}</td></tr>
			<tr><td>Country: </td><td>${client.getCountry()}</td></tr>
			<tr><td>City: </td><td>${client.getCity()}</td></tr>
			<tr><td>Phone Number: </td><td>${client.getPhoneNumber()}</td></tr>
			</table>
			<a href = "<@spring.url "/client/edit"/>">Edit profile</a>
			<@security.authorize access="isRememberMe()">
				<p>O yeep!
			</@security.authorize>
		</div>
	</div>
	</div>
</body>
</html>