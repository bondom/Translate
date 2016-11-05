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
			<#if settings??>
				Max number of advertisements for client: 
				${settings.maxNumberOfAdsForClient}<br>
				Max number of SENDED responses for translator: 
				${settings.maxNumberOfSendedRespondedAdsForTranslator}<br>
				Min hours between refreshings:
				${settings.minHoursBetweenRefreshings}<br>
				Initial pledge in percent, which must be payed by
				client for accepting Advertisement:
				${settings.initPledgeInPercent}%<br>
				Max number of advertisement, which can be rendered on
				one page for all users:
				${settings.maxNumberOfAdsOnOnePage}<br>
				Max number of responses, which can be rendered on
				one page for translators and clients:
				${settings.maxNumberOfRespondedAdsOnOnePage}<br>
				Max number of translators, which can be rendered on
				one page for all users:
				${settings.maxNumberTranslatorsOnOnePage}<br>
				Max number of NotChecked advertisements, 
				which can be rendered on one page for admin:
				${settings.maxNumberNotCheckedAdsOnOnePage}<br>
				<form action="<@spring.url "/bulbular/settings/edit"/>" method="Get">
					<button type="submit" class="btn btn-info"/>Change</button>
				</form>
			<#else>
				No Settings exist in data storage
			</#if>
		</div>
	</div>
	</div>
</body>
</html>