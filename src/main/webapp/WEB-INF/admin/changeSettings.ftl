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
				<form action = "<@spring.url "/bulbular/settings/saveEdits"/>" method = "Post">
					Max number of advertisements for client: 
					<@spring.bind "settings.maxNumberOfAdsForClient"/>
					<input type = "text" id = "name" name = "${spring.status.expression}" 
					value = "${spring.status.value!""}" class="form-control"/>
					</br>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list>
					
					<br>Max number of SENDED responses for translator: 
					<@spring.bind "settings.maxNumberOfSendedRespondedAdsForTranslator"/>
					<input type = "text" id = "name" name = "${spring.status.expression}" 
					value = "${spring.status.value!""}" class="form-control"/>
					</br>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list>

					<br>Min hours between refreshings:
					<@spring.bind "settings.minHoursBetweenRefreshings"/>
					<input type = "text" id = "name" name = "${spring.status.expression}" 
					value = "${spring.status.value!""}" class="form-control"/>
					</br>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list>

					<br>Initial pledge in percent, which must be payed by
					client for accepting Advertisement:
					<@spring.bind "settings.initPledgeInPercent"/>
					<input type = "text" id = "name" name = "${spring.status.expression}" 
					value = "${spring.status.value!""}" class="form-control"/>
					</br>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list>

					<br>Max number of advertisement, which can be rendered on
					one page for all users:
					<@spring.bind "settings.maxNumberOfAdsOnOnePage"/>
					<input type = "text" id = "name" name = "${spring.status.expression}" 
					value = "${spring.status.value!""}" class="form-control"/>
					</br>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list>

					<br>Max number of responses, which can be rendered on
					one page for translators and clients:
					<@spring.bind "settings.maxNumberOfRespondedAdsOnOnePage"/>
					<input type = "text" id = "name" name = "${spring.status.expression}" 
					value = "${spring.status.value!""}" class="form-control"/>
					</br>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list>

					<br>Max number of translators, which can be rendered on
					one page for all users:
					<@spring.bind "settings.maxNumberTranslatorsOnOnePage"/>
					<input type = "text" id = "name" name = "${spring.status.expression}" 
					value = "${spring.status.value!""}" class="form-control"/>
					</br>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list>

					<br>Max number of NotChecked advertisements, 
					which can be rendered on one page for admin:
					<@spring.bind "settings.maxNumberNotCheckedAdsOnOnePage"/>
					<input type = "text" id = "name" name = "${spring.status.expression}" 
					value = "${spring.status.value!""}" class="form-control"/>
					</br>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list>
					
					<button type="submit" class="btn btn-info"/>Save Edits</button>
					<input type="hidden"
								name="${_csrf.parameterName}"
								value="${_csrf.token}"/>
					<a href = "<@spring.url "/bulbular/settings"/>">Cancel</a>
				</form>
			<#else>
				No Settings exist in data storage
			</#if>
		</div>
	</div>
	</div>
</body>
</html>