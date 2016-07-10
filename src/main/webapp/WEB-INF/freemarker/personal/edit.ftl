<#ftl encoding="UTF-8">
<#import "/spring.ftl" as spring>
<html>
<head>
	<link rel="stylesheet" type = "text/css" href="<@spring.url "/resources/css/bootstrap.min.css"/>"/>         
	<script src="<@spring.url "/resources/js/bootstrap.min.js"/>"></script> 
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<#include "/fragments/authheader.ftl">
	<h5>Please upload a image</h5>
	<form method="post" action="<@spring.url "/personal/saveAvatar"/>" enctype="multipart/form-data">
		<input type="file" name="file"/>
		<input type="submit"/>
		<#if wrongFile??>
			<div class="alert alert-warning">${wrongFile}</div>
		</#if>
	</form>
	
	
	<form method="post" action="<@spring.url "/personal/saveEdits"/>">
	<table>
		<tr><td>Name: </td><td>
							<@spring.bind "user.name"/>
							<input type = "text" id = "name" name = "${(spring.status.expression)!"email"}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Name"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
		</td></tr>
		<tr><td>Surname: </td><td>
							<@spring.bind "user.surname"/>
							<input type = "text" id = "surname" name = "${(spring.status.expression)!"surname"}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Surname"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
		</td></tr>
		<tr><td>Email: </td><td>
							<@spring.bind "user.email"/>
							<input type = "text" id = "email" name = "${(spring.status.expression)!"email"}" 
							value = "${spring.status.value!""}" class="form-control" placeholder = "Email"/>
							</br>
							<#list spring.status.errorMessages as error>
								<div class="alert alert-warning">${error}</div>
							</#list>
							<#if emailExists??>
								<div class="alert alert-warning">${emailExists}</div>
							</#if>
		</td></tr>
		<tr><td>Phone Number: </td><td>
								<@spring.bind "user.phoneNumber"/>
								<input type = "text" id = "phoneNumber" name = "${(spring.status.expression)!"phoneNumber"}" 
								value = "${spring.status.value!""}" class="form-control" placeholder = "Phone number"/>
								</br>
								<#list spring.status.errorMessages as error>
									<div class="alert alert-warning">${error}</div>
								</#list>
		</td></tr>
		<tr><td><input type="submit"/></td></tr>
	</table>
	

</body>
</html>