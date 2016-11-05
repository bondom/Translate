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
		<@security.authorize access="hasRole('ROLE_CLIENT') or ! isAuthenticated()">
			<#include "/fragments/inittranslatorheader.ftl">
		</@security.authorize>
		<@security.authorize access="hasRole('ROLE_TRANSLATOR')">
			<#include "/fragments/authtranslatorheader.ftl">
		</@security.authorize>
		<div class="panel-body" style = "margin: 0px">
					<div>
						<p>Name: ${ad.getName()}
						<p>Publication Date: ${ad.publicationDateTime.toLocalDate()}
						<p>Description: ${ad.getDescription()}
						<p>Init Language: ${ad.getInitLanguage()}
						<p>Result Language${ad.getResultLanguage()}
						<p>TranslateType: ${ad.getTranslateType()}
						<p>Country: ${ad.getCountry()} City: ${ad.getCity()}
						<p>From: ${ad.getInitialDateTime()} To: ${ad.getFinishDateTime()}
						<p>Cost: ${ad.getCost()} ${ad.getCurrency()}
						<@security.authorize access="hasRole('ROLE_TRANSLATOR')">

							<#if ad.status.name()="SHOWED">
								<form action = "<@spring.url "/translator/response"/>" method="post" role="form">
									<@spring.formHiddenInput "ad.id"/>
									<button type = "submit" class="btn btn-info">
										Respond
									</button>
									<input type="hidden"
										name="${_csrf.parameterName}"
										value="${_csrf.token}"/>
								</form>
							</#if>
							<#if ad.status.name()=="PAYED">
								<div class="alert alert-success">
									Ad is already executed.
								</div>
							</#if>
							<#if ad.status.name()="ACCEPTED" ||
									ad.status.name()="REWORKING" ||
									ad.status.name()="NOTCHECKED" ||
									ad.status.name()="CHECKED" >
								<div class="alert alert-warning">
									Ad is being executed.
								</div>
							</#if>
						</@security.authorize>
					</div>
			
		</div>
	</div>
	</div>
</body>
</html>