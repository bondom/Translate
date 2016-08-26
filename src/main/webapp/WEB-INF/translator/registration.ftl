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
			<#include "/fragments/inittranslatorheader.ftl">
			<div class="panel-body" style = "margin: 0px">
			<h3>Registration</h3>
				<form action = "<@spring.url "/translator/registrationConfirm"/>" method = "post" role = "form">
					<div class="form-group col-xs-5" >
							<#if resultRegistration??>
								<div class="alert alert-danger">${resultRegistration}</div>
							</#if>

							<@spring.bind "translator.firstName"/>
							<input type = "text" id = "firstName" name = "${(spring.status.expression)!"firstName"}" 
							value = "${spring.status.value!""}" class="form-control" required="true" placeholder = "First Name"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<@spring.bind "translator.lastName"/>
							<input type = "text" id = "lastName" name = "${(spring.status.expression)!"lastName"}" 
							value = "${spring.status.value!""}" class="form-control" required="true" placeholder = "Last Name"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
							
							Birthday:
							<@spring.bind "translator.birthday"/>
							<input type = "text" id = "birthday" name = "${(spring.status.expression)!"birthday"}" 
							value = "${spring.status.value!""}" class="form-control" required="true" placeholder = "Birthday"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<@spring.bind "translator.country"/>
							<input list = "countries" id = "country" name = "${(spring.status.expression)!"country"}" 
							value = "${spring.status.value!""}" class="form-control" required="true" placeholder = "Country"/>
							</br>
							<datalist id="countries">
							    <option value="Ukraine">
							    <option value="Russia">
							    <option value="USA">
							  </datalist>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<@spring.bind "translator.city"/>
							<input type = "text" id = "city" name = "${(spring.status.expression)!"city"}" 
							value = "${spring.status.value!""}" class="form-control" required="true" placeholder = "City"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
								
							<#macro enumSelect selectName enumValues>
								<select name = "${selectName}" multiple = "multiple">
									<#list enumValues as enum>
										<option value = "${enum}">${enum}</option>
									</#list>
								</select>
							</#macro>
							<@enumSelect "selectedLanguages" languages/>
							
							<@spring.bind "translator.phoneNumber"/>
							<input type = "text" id = "phoneNumber" name = "${(spring.status.expression)!"phoneNumber"}" 
							value = "${spring.status.value!""}" class="form-control" required="true" placeholder = "Phone number"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>

							<@spring.bind "translator.email"/>
							<input type = "text" id = "email" name = "${(spring.status.expression)!"email"}" 
							value = "${spring.status.value!""}" class="form-control" required="true" placeholder = "Email"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
							
							<@spring.bind "translator.password"/>
							<input type = "password" id = "password" name = "${(spring.status.expression)!"password"}" 
							class="form-control" required="true" placeholder = "Password"/>
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