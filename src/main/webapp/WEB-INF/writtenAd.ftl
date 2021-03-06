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
						<p>End Date: ${ad.getEndDate()} 
						<p>File:
						 <@security.authorize access="hasRole('ROLE_TRANSLATOR')">
							<a href="<@spring.url "/download/${ad.id}"/>" target="_blank" >
								${ad.document.fileName}
							</a>
						 </@security.authorize>
						 <@security.authorize access="hasRole('ROLE_CLIENT') or ! isAuthenticated()">
								${ad.document.fileName}
						 </@security.authorize>
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
						</@security.authorize>
					</div>
			
		</div>
	</div>
	</div>
</body>
</html>