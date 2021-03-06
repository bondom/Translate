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
			<#if respondedAds?has_content>
				<#list respondedAds as respondedAd>
					<#assign ad = respondedAd.ad>
					<p><a href = "<@spring.url "/ads/${ad.getId()}"/>">${ad.getName()}</a>
					${respondedAd.getDateTimeOfResponse()}
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
					<#assign adstatus = respondedAd.status>
					<p>Status:${adstatus}
				</#list>
				</br>
				<#list 1..numberOfPages as page>
					<a href="<@spring.url "/translator/responses?page=${page}"/>">${page}</a>&nbsp
				</#list>
			<#else>
				<p> You haven't responded yet to any one advertisement
				<p><a href = "<@spring.url "/adsw"/>">Find advertisements</a>
			</#if>
		</div>
	</div>
	</div>
</body>
</html>