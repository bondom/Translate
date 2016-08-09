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
			<#if responsedAds?has_content>
				<#list responsedAds as responsedAd>
					<#assign translator = responsedAd.translator>
					<#assign ad = responsedAd.ad>
					<p><a href = "<@spring.url "/translators/${translator.getId()}"/>">${translator.getFirstName()} ${translator.getLastName()}</a>	responsed on
					<a href = "<@spring.url "/ads/${ad.getId()}"/>">${ad.getName()}</a>				
					<p>at ${responsedAd.getDateTimeOfResponse()}
					<#assign adstatus = responsedAd.status>
					<p>Status:${adstatus}
					<#if adstatus.name()="SENDED">
						<a href="<@spring.url "/client/reject?radId=${responsedAd.getId()}"/>">Reject</a>						
						<a href="<@spring.url "/client/accept?radId=${responsedAd.getId()}"/>">Accept</a>
					</#if>
				</#list>
			</#if>
		</div>
	</div>
	</div>
</body>
</html>