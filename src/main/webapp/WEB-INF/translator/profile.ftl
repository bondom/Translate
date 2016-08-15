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
		<#include "/fragments/authtranslatorheader.ftl">
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
			Email: ${translator.getEmail()} <a href="<@spring.url "/translator/email"/>">Edit</a>
			<#if translator.getEmailStatus().name()=="NOTCONFIRMED">
				<div class="alert alert-warning">
						You have not confirmed your email.
				</div>
				<a href="<@spring.url "/translator/email-confirm"/>">Confirm</a></br>
			</#if>
			</br>Password:***** <a href="<@spring.url "/translator/password"/>">Edit</a></br>
				<table>
				<tr><td>Rating: </td><td>${translator.getRating()}</td></tr>
				<tr><td>Number od executed orders: </td><td>${translator.getNumberOfExecutedAds()}</td></tr>
				<tr><td>Languages: </td>
					<td><#list translator.languages as language>
						${language}
						</#list>
					</td>
				</tr>
				<tr><td>AddedInfo: </td>
					<td><#if translator.addedInfo??>
							${translator.getAddedInfo()}
						</#if>
					</td>
				</tr>
				<tr><td>First Name: </td><td>${translator.getFirstName()}</td></tr>
				<tr><td>Last Name: </td><td>${translator.getLastName()}</td></tr>
				<tr><td>Country: </td><td>${translator.getCountry()}</td></tr>
				<tr><td>City: </td><td>${translator.getCity()}</td></tr>
				<tr><td>Date of birth: </td><td>${translator.getBirthday()}</td></tr>
				<tr><td>Phone Number: </td><td>${translator.getPhoneNumber()}</td></tr>
				<tr><td>Registration Time: </td><td>${translator.getRegistrationTime()}</td></tr>
				</table>
				<a href = "<@spring.url "/translator/edit"/>">Edit profile</a>
			<@security.authorize access="isRememberMe()">
				<p>O yeep!
			</@security.authorize>
		</div>
	</div>
	</div>
</body>
</html>