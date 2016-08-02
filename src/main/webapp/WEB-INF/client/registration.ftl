<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring/>
<html>
<head>
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/css/bootstrap.min.css"/>"/>         
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Registration Form</title>
</head>
<body>
	<div class="container">
		<div class="panel panel-default">
			<#include "/fragments/initclientheader.ftl">
			<div class="panel-body" style = "margin: 0px">
			<h3>Registration</h3>
				<form action = "<@spring.url "/client/registrationConfirm"/>" method = "Post" role = "form">
					<div class="form-group col-xs-5" >
							<#if error??>
								<div class="alert alert-danger">${error}</div>
							</#if>

							<@spring.bind "client.firstName"/>
							<input type = "text" id = "firstName" name = "${spring.status.expression}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "First Name"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<@spring.bind "client.lastName"/>
							<input type = "text" id = "lastName" name = "${spring.status.expression}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Last Name"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
							
							Birthday:
							<@spring.bind "client.birthday"/>
							<input type = "text" id = "birthday" name = "${spring.status.expression}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Birthday"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<#--<@spring.bind "client.birthday.month"/>
							<input type = "text" id = "birthday.month" name = "${(spring.status.expression)!"birthday.month"}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Month"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<@spring.bind "client.birthday.year"/>
							<input type = "text" id = "birthday.year" name = "${(spring.status.expression)!"birthday.year"}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Year"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>-->
							
							<@spring.bind "client.country"/>
							<input type = "text" id = "country" name = "${spring.status.expression}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Country"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<@spring.bind "client.city"/>
							<input type = "text" id = "city" name = "${spring.status.expression}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "City"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<@spring.bind "client.phoneNumber"/>
							<input type = "text" id = "phoneNumber" name = "${spring.status.expression}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Phone number"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<@spring.bind "client.email"/>
							<input type = "text" id = "email" name = "${spring.status.expression}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Email"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
							
							<@spring.bind "client.password"/>
							<input type = "password" id = "password" name = "${spring.status.expression}" 
							class="form-control" placeholder = "Password"/>
							<br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
							
							<button type = "submit" class="btn btn-info">
								Sign In
							</button>
							<input type="hidden"
									name="${_csrf.parameterName}"
									value="${_csrf.token}"/>
						</div>
				</form>
			</div>
		</div>
	</div>
	</div>
	
</body>
</html>