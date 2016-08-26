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
				<div class="alert alert-danger">
					${error}
				</div>
			</#if>
			<#if respondedAd??>
				<div>
					<#assign ad = respondedAd.ad>
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
						<a href="<@spring.url "/translator/download/${ad.id}"/>" target="_blank" >
							${ad.document.fileName}
						</a>
					</#if>
					<p>Cost: ${ad.getCost()} ${ad.getCurrency()}
					<#if ad.status.name()=='ACCEPTED'>
					<form action = "<@spring.url "/translator/finish"/>" method="post" role="form">
						<input name="id" type="hidden" value="${ad.getId()}"/>
						<button type = "submit" class="btn btn-info">
							Execute
						</button>
						<input type="hidden"
							name="${_csrf.parameterName}"
							value="${_csrf.token}"/>
					</form>
					<#else>
						<br>Your answer is sended to administrator. Wait please..
					</#if>
				</div>
				<#else>
				<div>
					You don't execute order
				</div>
			</#if>
			
		</div>
	</div>
	</div>
</body>
</html>