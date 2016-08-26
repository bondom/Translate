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
				<#if createAd==true>
					<#assign formUrl = "/client/saveWrittenAd?${_csrf.parameterName}=${_csrf.token}"/>
				<#else>
					<#assign formUrl = "/client/saveWrittenAdEdits?${_csrf.parameterName}=${_csrf.token}"/>
				</#if>
				<#if Editmsg??>
					<div class="alert alert-success">${Editmsg}</div>
				</#if>
				
				<form method="post" action="<@spring.url formUrl/>" enctype="multipart/form-data">
				<table>
					<tr><td>Name: </td><td>
										<@spring.bind "ad.name"/>
										<input type = "text" id = "name" name = "${spring.status.expression}" 
										value = "${spring.status.value!""}" class="form-control" placeholder = "Name"/>
										</br>
										<#list spring.status.errorMessages as error>
											<div class="alert alert-warning">${error}</div>
										</#list>
					</td></tr>
					<tr><td>Description: </td><td>
										<@spring.bind "ad.description"/>
										<textarea id = "addedInfo" name = "${spring.status.expression}" cols = 50 rows = 10>
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
					<tr><td>End Date: </td><td>
										<@spring.bind "ad.endDate"/>
										<input type = "text" id = "endDate" name = "${spring.status.expression}" 
										value = "${spring.status.value!""}" class="form-control" placeholder = "End Date"/>
										</br>
										<#list spring.status.errorMessages as error>
											<div class="alert alert-warning">${error}</div>
										</#list>
					</td></tr>
					<tr><td>Cost: </td><td>
										<@spring.bind "ad.cost"/>
										<input type = "text" id = "cost" name = "${spring.status.expression}" 
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
					<tr><td>File: </td><td>
										<input type="file" name="multipartFile"/></br></br>${(ad.document.fileName)!""}
										</br>
										</br>
										<#if error??>
											<div class="alert alert-warning">${error}</div>
										</#if>
					</td></tr>
					<tr><td></td><td></td><@spring.bind "ad.id"/>
										 <input type = "hidden" name = "${spring.status.expression}" 
										 value = "${(spring.status.value)!""}" />
					</tr>
					<tr><td></td><td></td><@spring.bind "ad.translateType"/>
										 <input type = "hidden" name = "${spring.status.expression}" 
										 value = "${spring.status.value}" />
					</tr>
					<tr><td><button type="submit" class="btn btn-info"/>Save</button></td>
						<td></td>
					</tr>
					
				</table>
				</form>
		</div>
	</div>
	</div>
</body>
</html>