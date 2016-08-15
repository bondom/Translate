<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring>
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
				
				<form method="post" autocomplete="false" action="<@spring.url "/translator/saveEmail"/>">
					<h4>E-mail</h4>
						
					<@spring.bind "changeEmailBean.newEmail"/>
					<label>New e-mail:</label><input type = "text" class="form-control" name = "${spring.status.expression}" value = "${spring.status.value!""}"/>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list><br>
					<#if duplicateEmail??>
						<div class="alert alert-warning">${duplicateEmail}</div>
					</#if><br>
					
					<@spring.bind "changeEmailBean.newEmailAgain"/>
					<p><label>New e-mail again:</label><input type = "text" autocomplete="false" class="form-control" name = "${(spring.status.expression)}" value = "${spring.status.value!""}"/></p>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list><br>
					
					<@spring.bind "changeEmailBean.currentPassword"/>
					<p><label>Your current password:</label><input type = "password" autocomplete="new-password" class="form-control" name = "${(spring.status.expression)}" value=""/></p>
					<#if invalidPassword??>
						<div class="alert alert-warning">${invalidPassword}</div>
					</#if><br>
					
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list><br>
						
					<button type = "submit" class="btn btn-info">
						Save
					</button>
					
					<a href = "<@spring.url "/translator/profile"/>" role="button">Cancel</a>					
					<input type="hidden"
							name="${_csrf.parameterName}"
							value="${_csrf.token}"/>
						
				</form>
			</div>
		</div>
	</div>
</body>
</html>