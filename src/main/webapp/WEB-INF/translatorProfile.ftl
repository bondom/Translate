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
		<@security.authorize access="hasRole('ROLE_TRANSLATOR') or ! isAuthenticated()">
			<#include "/fragments/initclientheader.ftl">
		</@security.authorize>
		<@security.authorize access="hasRole('ROLE_CLIENT')">
			<#include "/fragments/authclientheader.ftl">
		</@security.authorize>
		<@security.authorize access="hasRole('ROLE_ADMIN')">
			<#include "/fragments/authadminheader.ftl">
		</@security.authorize>
		<div class="panel-body" style = "margin: 0px">
				<img  src="data:image/jpeg;base64,${image!""}" />
				<table>
				<tr><td>First Name: </td><td>${translator.getFirstName()}</td></tr>
				<tr><td>Last Name: </td><td>${translator.getLastName()}</td></tr>
				<tr><td>Rating: </td><td>${translator.getRating()}</td></tr>
				<tr><td>Number of executed orders: </td><td>${translator.getNumberOfExecutedAds()}</td></tr>
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
				<tr><td>Country: </td><td>${translator.getCountry()}</td></tr>
				<tr><td>City: </td><td>${translator.getCity()}</td></tr>
				<tr><td>Date of birth: </td><td>${translator.getBirthday()}</td></tr>
				</table>
				<br>
				Comments:<br>
				<#assign comments = translator.comments>
				<#if comments?has_content>
					<#list comments as comment>
						Client name:${comment.getClientName()}&nbsp${comment.getCreatingDate()}<br>
						${comment.getText()}<br>
					</#list>
				<#else>					
					This translator hasn't yet comments
				</#if>
				
				<@security.authorize access="hasRole('ROLE_CLIENT')">
					<form action = "<@spring.url "/client/addComment"/>" method = "Post" role = "form">
					<div class="form-group col-xs-5" >
				
							<@spring.bind "comment.text"/>
							<textarea id = "addedInfo" name = "${spring.status.expression}" cols = 40 rows = 4>
							</textarea>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
							<br>
							<input type="hidden"
									name="translatorId"
									value="${translator.getId()}"/>
							<button type = "submit" class="btn btn-info">
								Add comment
							</button>
							<input type="hidden"
									name="${_csrf.parameterName}"
									value="${_csrf.token}"/>
						</div>
					</form>
				</@security.authorize>
		</div>
	</div>
	</div>
</body>
</html>