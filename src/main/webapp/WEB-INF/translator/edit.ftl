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
				<h5>Please upload a image</h5>
				<form method="post" action="<@spring.url "/translator/saveAvatar?${_csrf.parameterName}=${_csrf.token}"/>" enctype="multipart/form-data">
					<input type="file" name="file"/>
					<input type="submit"/>
					<#if wrongFile??>
						<div class="alert alert-warning">${wrongFile}</div>
					</#if>
				</form>
				
				<form method="post" action="<@spring.url "/translator/saveEdits"/>">
				<table>
					<tr><td>First Name: </td><td>
										<@spring.bind "translator.firstName"/>
										<input type = "text" id = "firstName" name = "${(spring.status.expression)!"firstName"}" 
										value = "${spring.status.value!""}" class="form-control" placeholder = "First Name"/>
										</br>
										<#list spring.status.errorMessages as error>
											<div class="alert alert-warning">${error}</div>
										</#list>
					</td></tr>
					<tr><td>Last Name: </td><td>
										<@spring.bind "translator.lastName"/>
										<input type = "text" id = "lastName" name = "${(spring.status.expression)!"lastName"}" 
										value = "${spring.status.value!""}" class="form-control" placeholder = "Last Name"/>
										</br>
										<#list spring.status.errorMessages as error>
											<div class="alert alert-warning">${error}</div>
										</#list>
					</td></tr>
					<tr><td>Country: </td><td>
										<@spring.bind "translator.country"/>
										<input type = "text" id = "country" name = "${(spring.status.expression)!"country"}" 
										value = "${spring.status.value!""}" class="form-control" placeholder = "Country"/>
										</br>
										<#list spring.status.errorMessages as error>
											<div class="alert alert-warning">${error}</div>
										</#list>
					</td></tr>
					<tr><td>City: </td><td>
										<@spring.bind "translator.city"/>
										<input type = "text" id = "city" name = "${(spring.status.expression)!"city"}" 
										value = "${spring.status.value!""}" class="form-control" placeholder = "City"/>
										</br>
										<#list spring.status.errorMessages as error>
											<div class="alert alert-warning">${error}</div>
										</#list>
					</td></tr>
					<tr><td>Date of Birth: </td><td>
										<@spring.bind "translator.birthday"/>
										<input type = "text" id = "birthday" name = "${(spring.status.expression)!"birthday"}" 
										value = "${spring.status.value!""}" class="form-control" placeholder = "Date of birth"/>
										</br>
										<#list spring.status.errorMessages as error>
											<div class="alert alert-warning">${error}</div>
										</#list>
					</td></tr>
					<tr><td>Added info: </td><td>
										<@spring.bind "translator.addedInfo"/>
										<textarea id = "addedInfo" name = "${(spring.status.expression)!"addedInfo"}" cols = 20 rows = 4>
										<#if translator.addedInfo??>
										${translator.addedInfo}
										</#if>
										</textarea>
										<#list spring.status.errorMessages as error>
											<div class="alert alert-warning">${error}</div>
										</#list>
					</td></tr>
					<tr><td>Email: </td><td>
										<@spring.bind "translator.email"/>
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
											<@spring.bind "translator.phoneNumber"/>
											<input type = "text" id = "phoneNumber" name = "${(spring.status.expression)!"phoneNumber"}" 
											value = "${spring.status.value!""}" class="form-control" placeholder = "Phone number"/>
											</br>
											<#list spring.status.errorMessages as error>
												<div class="alert alert-warning">${error}</div>
											</#list>
					</td></tr>
					<tr><td><input type="submit"/></td>
						<td><input type="hidden"
								name="${_csrf.parameterName}"
								value="${_csrf.token}"/>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</body>
</html>