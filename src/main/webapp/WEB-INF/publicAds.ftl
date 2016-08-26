<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring>
<#assign security=JspTaglibs["http://www.springframework.org/security/tags"] />
<html>
<head>
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/css/bootstrap.min.css"/>"/>         
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<style type="text/css">
		div.inline { float:left; margin: 10px;}
		.clearBoth { clear:both; }
	</style>
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
				<form method="get" action="<@spring.url "/ads"/>">
					<br>Translate type: <@spring.formSingleSelect "search.translateType" translateTypes/>
					<br>Country: <@spring.formSingleSelect "search.country" countries/>
					<br>City: <@spring.formSingleSelect "search.city" cities/>
					<br>Init Language: <@spring.formSingleSelect "search.initLanguage" languages/>
					<#if error??><div class="alert alert-danger">${error}</div></#if>
					<br>Result Language: <@spring.formSingleSelect "search.resultLanguage" languages/>
					<br>Cost: from <@spring.formInput "search.minCost" /> to <@spring.formInput "search.maxCost" />  
					<@spring.formSingleSelect "search.currency" currencies/>
					<button type="submit" class = "btn btn-info">
						Find
					</button>
				</form>
			</div>
			<#list adsView as adView>
				<#assign ad=adView.ad>
				<div>
					<p><a href = "<@spring.url "/ads/${ad.getId()}"/>">${ad.getName()}</a>
					<#if adView.respondingTime??>
						<div class="alert alert-warning">
							You responded on this advertisement at ${adView.respondingTime}.
						</div>
					</#if>
					<p>TranslateType: ${ad.getTranslateType()}
					<p>Init Language: ${ad.getInitLanguage()}
					<p>Result Language: ${ad.getResultLanguage()}
					<p>${adView.getMessageWithPublishingTime()}
				</div>
				</br>
			<#else>
				<br><p>There is no such advertisements.<br>
			</#list>
			<#if numberOfPages gt 0>
				<#list 1..numberOfPages as page>
					<div class="inline">
					<form method = "get" action="<@spring.url "/ads"/>">
						<input type="hidden" name = "page" value="${page}"/>
						<@spring.formHiddenInput "search.translateType"/>
						<@spring.formHiddenInput "search.country"/>
						<@spring.formHiddenInput "search.city"/>
						<@spring.formHiddenInput "search.initLanguage"/>
						<@spring.formHiddenInput "search.resultLanguage"/>
						<@spring.formHiddenInput "search.minCost"/>
						<@spring.formHiddenInput "search.maxCost"/>
						<@spring.formHiddenInput "search.currency"/>
						<button type="submit" class="btn btn-info">${page}</button>
					</form>&nbsp
					</div>	
				</#list>
			</#if>
		</div>
	</div>
	</div>
</body>
</html>