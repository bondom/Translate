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
			<#if error??>
				<div class="alert alert-danger">${error}</div>
			</#if>
			<#if success??>
				<div class="alert alert-success">${success}</div>
			</#if>
			<#list adStatusesAndMessages as adStatusMessage>
				<div>
					Translate type:${adStatusMessage.translateType}<br>
					Status:${adStatusMessage.adStatus}<br>
					Message for client:${adStatusMessage.messageForClient}
					<br>
					Message for translator:${adStatusMessage.messageForTranslator}
					<form action = "<@spring.url "/bulbular/changeMsgs"/>" method = "Get">
						<input type="hidden" name="adStatus" value="${adStatusMessage.adStatus}"/>
						<input type="hidden" name="translateType" value="${adStatusMessage.translateType}"/>
						<button type="submit" class = "btn btn-info">
							Change
						</button>
					</form>
				</div>
				</br>
			</#list>
		</div>
	</div>
	</div>
</body>
</html>