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
			<#list translatorsView as transView>
				<#assign translator=transView.translator>
				<div>
					<img  src="data:image/jpeg;base64,${transView.getAvatar()!""}" />
					<p><a href = "<@spring.url "/translators/${translator.getId()}"/>">${translator.getFirstName()}&nbsp&nbsp${translator.getLastName()}</a>
					<p>Languages: <#list translator.languages as language>
								  		${language}
								  </#list>
					<p>${transView.getMessageWithPublishingTime()}
					</br>
				</div>
				</br>
			</#list>
			
			<#if numberOfPages gt 1>
				<#list 1..numberOfPages as page>
					<a href="<@spring.url "/translators?page=${page}"/>">${page}</a>&nbsp
				</#list>
			</#if>
		</div>
	</div>
	</div>
</body>
</html>