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
			<#if msg??>
					<div class="alert alert-success">${msg}</div>
			</#if>
			<#if ads?? && ads?size gt 0>
				<#list ads as ad>
					<div>
						<p>Name: ${ad.getName()}
						<p>Description: ${ad.getDescription()}
						<p>Init Language: ${ad.getInitLanguage()}
						<p>Result Language: ${ad.getResultLanguage()}
						<p>TranslateType: ${ad.getTranslateType()}
						<#if ad.translateType.name()=="ORAL">
						<p>Country: ${ad.getCountry()} City: ${ad.getCity()}
						<p>From: ${ad.getInitialDateTime()} To: ${ad.getFinishDateTime()}
						</#if>
						<#if ad.translateType.name()=="WRITTEN">
						<p>End Date: ${ad.getEndDate()} 
						<p>File:
						<a href="<@spring.url "/translator/download/${ad.id}"/>" target="_blank" >
								${ad.document.fileName}
						</a>
						</#if>
						<p>Status: ${ad.getStatus()}
						<#if ad.status.name()=="SHOWED">
						<form action = "<@spring.url "/client/ads/delete"/>" method = "Post" role = "form">
							<input name="adId" type="hidden" value="${ad.getId()}"/>
							<button type = "submit" class="btn btn-info">
								Delete
							</button>
							<input type="hidden"
									name="${_csrf.parameterName}"
									value="${_csrf.token}"/>
						</form>
						<p>Refreshed:${ad.getPublicationDateTime()}
						<form action = "<@spring.url "/client/ads/refresh"/>" method = "Post" role = "form">
							<input name="adId" type="hidden" value="${ad.getId()}"/>
							<button type = "submit" class="btn btn-info">
								Refresh Date
							</button>
							<input type="hidden"
									name="${_csrf.parameterName}"
									value="${_csrf.token}"/>
						</form>
						<a href = "<@spring.url "/client/ads/edit?adId=${ad.getId()}"/>">EDIT</a>
						</#if>
					</div>
					</br>
				</#list>
			<#else>
				You haven't advertisements yet.</br>
			</#if>
			<a href = "<@spring.url "/client/adbuilder"/>" class="btn btn-info" role="button">Create Ad</a>
		</div>
	</div>
	</div>
</body>
</html>