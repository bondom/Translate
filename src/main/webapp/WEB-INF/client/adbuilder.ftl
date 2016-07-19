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
		<#include "/fragments/authclientheader.ftl">
		<div class="panel-body" style = "margin: 0px">
				<form method="post" action="<@spring.url "/client/saveAd"/>">
				<table>
					<tr><td>Name: </td><td>
										<@spring.bind "ad.name"/>
										<input type = "text" id = "name" name = "${(spring.status.expression)!"name"}" 
										value = "${spring.status.value!""}" class="form-control" placeholder = "Name"/>
										</br>
										<#list spring.status.errorMessages as error>
											<div class="alert alert-warning">${error}</div>
										</#list>
					</td></tr>
					<tr><td>Description: </td><td>
										<@spring.bind "ad.description"/>
										<textarea id = "addedInfo" name = "${(spring.status.expression)!"description"}" cols = 50 rows = 10>
											${spring.status.value!""}
										</textarea>
										</br>
										<#list spring.status.errorMessages as error>
											<div class="alert alert-warning">${error}</div>
										</#list>
					</td></tr>
					<tr><td>Init Language: </td><td>
										<@spring.formSingleSelect "ad.initLanguage" languages/>
										<div><@spring.showErrors "</br>" "alert alert-warning"/></div>
					</td></tr>
					<tr><td>Result Language: </td><td>
										<@spring.formSingleSelect "ad.resultLanguage" languages/>
										<@spring.showErrors "</br>" "alert alert-warning"/>
					</td></tr>
					<tr><td>Country: </td><td>
										<@spring.bind "ad.country"/>
										<input type = "text" id = "country" name = "${(spring.status.expression)!"country"}" 
										value = "${country!""}" class="form-control" placeholder = "Country"/>
										<div><@spring.showErrors " " "alert alert-warning"/></div>
					</td></tr>
					<tr><td>City: </td><td>
										<@spring.bind "ad.city"/>
										<input type = "text" id = "city" name = "${(spring.status.expression)!"city"}" 
										value = "${city!""}" class="form-control" placeholder = "City"/>
										</br>
										<#list spring.status.errorMessages as error>
											<div class="alert alert-warning">${error}</div>
										</#list>
					</td></tr>
					<tr><td>Type of translation: </td><td>
										<@spring.formSingleSelect "ad.translateType" translateTypes/>
										</br>
										<#list spring.status.errorMessages as error>
											<div class="alert alert-warning">${error}</div>
										</#list>
					</td></tr>
					<tr><td>End Date: </td><td>
											<@spring.bind "ad.endDate"/>
											<input type = "text" id = "endDate" name = "${(spring.status.expression)!"endDate"}" 
											value = "${spring.status.value!""}" class="form-control" placeholder = "End Date"/>
											</br>
											<#list spring.status.errorMessages as error>
												<div class="alert alert-warning">${error}</div>
											</#list>
					</td></tr>
					<tr><td>Cost: </td><td>
											<@spring.bind "ad.cost"/>
											<input type = "text" id = "cost" name = "${(spring.status.expression)!"cost"}" 
											value = "${spring.status.value!""}" class="form-control" placeholder = "Cost"/>
											</br>
											<#list spring.status.errorMessages as error>
												<div class="alert alert-warning">${error}</div>
											</#list>
					</td></tr>
					<tr><td>Currency: </td><td>
											<@spring.formSingleSelect "ad.currency" currencies/>
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
				</form>
		</div>
	</div>
	</div>
</body>
</html>