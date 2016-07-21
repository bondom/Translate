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
			<#if ads??>
				<#list ads as ad>
					<div>
						<p>Name: ${ad.getName()}
						<p>Country: ${ad.getCountry()} City: ${ad.getCity()}
						<p>Description: ${ad.getDescription()}
						<p>Init Language: ${ad.getInitLanguage()}
						<p>Result Language: ${ad.getResultLanguage()}
						<p>Creating Date: ${ad.getCreationDateTime()}
						<p>Status: ${ad.getStatus()}
						<p><a href = "<@spring.url "/client/ads/delete?adId=${ad.getId()}"/>">DELETE</a>
						<p><a href = "<@spring.url "/client/ads/edit?adId=${ad.getId()}"/>">EDIT</a>
					</div>
					</br>
				</#list>
			</#if>
			<a href = "<@spring.url "/client/adbuilder"/>" class="btn btn-info" role="button">Create Ad</a>
		</div>
	</div>
	</div>
</body>
</html>