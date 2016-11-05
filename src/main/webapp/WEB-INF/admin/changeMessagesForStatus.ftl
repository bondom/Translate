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
			<div>
				<form action = "<@spring.url "/bulbular/saveChanges"/>" method = "Post">
					
					<@spring.bind "adStatusMessage.translateType"/>
					Translate type:${spring.status.value!""}
					<input type = "hidden" name = "${spring.status.expression}" 
						value = "${spring.status.value!""}" class="form-control"/>
					<br>
					
					<@spring.bind "adStatusMessage.adStatus"/>
					Status:${spring.status.value!""}
					<input type = "hidden" name = "${spring.status.expression}" 
						value = "${spring.status.value!""}" class="form-control"/>
					<br>
					Message for client:
					<@spring.bind "adStatusMessage.messageForClient"/>
					<input type = "text" name = "${spring.status.expression}" 
						value = "${spring.status.value!""}" class="form-control"/>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list>
					<br>
					Message for translator:
					<@spring.bind "adStatusMessage.messageForTranslator"/>
					<input type = "text" name = "${spring.status.expression}" 
						value = "${spring.status.value!""}" class="form-control"/>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list>
					<br>
					<button type="submit" class="btn btn-info">
						Update
					</button>
					<input type="hidden"
								name="${_csrf.parameterName}"
								value="${_csrf.token}"/>
				</form>
				<a href = "<@spring.url "/bulbular/admsgs"/>">Cancel</a>
			</div>
		</div>
	</div>
	</div>
</body>
</html>