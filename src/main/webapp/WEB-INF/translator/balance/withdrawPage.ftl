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
			<#if success??>
				<div class="alert alert-success">${success}</div>
			</#if>
			<#if error??>
				<div class="alert alert-danger">${error}</div>
			</#if>
			Your balance is: ${balance}
			<form action="<@spring.url "/translator/balance/withdraw"/>" method="POST">
				<@spring.bind "fundsBean.funds"/>
				Amount: <input type="text" name = "${spring.status.expression}" placeholder="0.0"> 
				<button class="btn btn-info">
					Withdraw
				</button>
				<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
				</#list><br>
				<input type="hidden"
					name="${_csrf.parameterName}"
					value="${_csrf.token}"/>
			</form>
		</div>
	</div>
	</div>
</body>
</html>