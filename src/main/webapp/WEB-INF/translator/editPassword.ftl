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
				
				<form method="post" autocomplete="false" action="<@spring.url "/translator/savePassword"/>">
					<h4>Password</h4>
					
					<@spring.bind "changePasswordBean.newPassword"/>
					<p><label>New password:</label><input type = "password" autocomplete="new-password" class="form-control" name = "${(spring.status.expression)}" value = ""/></p>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list><br>
					<@spring.bind "changePasswordBean.newPasswordAgain"/>
					<p><label>New password again:</label><input type = "password" autocomplete="new-password" class="form-control" name = "${(spring.status.expression)}" value=""/></p>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list><br>
					<@spring.bind "changePasswordBean.oldPassword"/>
					<label>Current password:</label><input type = "password" autocomplete="new-password" class="form-control" name = "${spring.status.expression}" value = ""/>
					<#list spring.status.errorMessages as error>
						<div class="alert alert-warning">${error}</div>
					</#list><br>
					<#if wrongOldPassword??>
						<div class="alert alert-warning">${wrongOldPassword}</div>
					</#if><br>
					
						
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