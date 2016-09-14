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
			<#include "/fragments/authadminheader.ftl">
		<div class="panel-body" style = "margin: 0px">
			
			<#list notCheckedAds as ad>
				<div>
					<p>Name: ${ad.getName()}
					<p>Publication Date: ${ad.publicationDateTime.toLocalDate()}
					<p>Description: ${ad.getDescription()}
					<p>Init Language: ${ad.getInitLanguage()}
					<p>Result Language${ad.getResultLanguage()}
					<p>TranslateType: ${ad.getTranslateType()}
					<#if ad.translateType.name()=="ORAL">
						<p>Country: ${ad.getCountry()} City: ${ad.getCity()}
						<p>From: ${ad.getInitialDateTime()} To: ${ad.getFinishDateTime()}
					</#if>
					<#if ad.translateType.name()=="WRITTEN">
						<p>End Date: ${ad.getEndDate()} 
						<p>File:
						 <@security.authorize access="hasRole('ROLE_TRANSLATOR') or hasRole('ROLE_ADMIN')">
							<a href="<@spring.url "/translator/download/${ad.id}"/>" target="_blank" >
								${ad.document.fileName}
							</a>
						 </@security.authorize>
						 <@security.authorize access="hasRole('ROLE_CLIENT') or ! isAuthenticated()">
								${ad.document.fileName}
						 </@security.authorize>
					</#if>
					<p>Cost: ${ad.getCost()} ${ad.getCurrency()}
				</div>
				</br>
			</#list>
		<#list 1..numberOfPages as page>
			<a href="<@spring.url "/ads?page=${page}"/>">${page}</a>&nbsp
		</#list>
		</div>
	</div>
	</div>
</body>
</html>