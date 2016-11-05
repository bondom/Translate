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
			<#include "/fragments/authadminheader.ftl">
		<div class="panel-body" style = "margin: 0px">
			<a href="<@spring.url "/bulbular/adso"/>">Written Advertisements</a>
			&nbsp&nbsp&nbspOral Advertisements<br>
			
			<form action  = "<@spring.url "/bulbular/adso"/>" method="get">
				Choose status of advertisement:<br>
				<@spring.formSingleSelect "search.adStatus" adStatuses/>
				<button type="submit" class="btn btn-info">
					Search
				</button>			
			</form>
			<#if ads??>
				<#list ads as ad>
					<div>
						<p>Name: ${ad.getName()}
						<p>Publication Date: ${ad.publicationDateTime.toLocalDate()}
						<p>Description: ${ad.getDescription()}
						<p>Init Language: ${ad.getInitLanguage()}
						<p>Result Language${ad.getResultLanguage()}
						<p>Cost: ${ad.getCost()} ${ad.getCurrency()}
						<p>Country: ${ad.getCountry()} City: ${ad.getCity()}
						<p>From: ${ad.getInitialDateTime()} To: ${ad.getFinishDateTime()}
					</div>
					</br>
				<#else>
					There is no such advertisement.
				</#list>
			</#if>

			<#if numberOfPages gt 1>
				<#list 1..numberOfPages as page>
					<form method = "get" action="<@spring.url "/bulbular/adso"/>">
						<input type="hidden" name = "page" value="${page}"/>
						<button type="submit" class="btn btn-info">${page}</button>
					</form>&nbsp
				</#list>
			</#if>
		</div>
	</div>
	</div>
</body>
</html>