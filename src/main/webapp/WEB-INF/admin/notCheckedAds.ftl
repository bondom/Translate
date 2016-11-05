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
			<#if success??>
				<div class="alert alert-success">${success}</div>
			</#if>
			<#if error??>
				<div class="alert alert-error">${error}</div>
			</#if>
			<#list notCheckedAds as ad>
				<div>
					<p>Name: ${ad.getName()}
					<p>Publication Date: ${ad.publicationDateTime.toLocalDate()}
					<p>Description: ${ad.getDescription()}
					<p>Init Language: ${ad.getInitLanguage()}
					<p>Result Language${ad.getResultLanguage()}
					<p>TranslateType: ${ad.getTranslateType()}
					<p>End Date: ${ad.getEndDate()} 
					<p>File:
					<a href="<@spring.url "/download/${ad.id}"/>" target="_blank" >
						${ad.document.fileName}
					</a>
					<p>Result File:
					<a href="<@spring.url "/downloadr/${ad.id}"/>" target="_blank" >
						${ad.resultDocument.fileName}
					</a>
					<p>Cost: ${ad.getCost()} ${ad.getCurrency()}
					<form action = "<@spring.url "/bulbular/markAsChecked"/>" method = "Post" role = "form">
						<input name="adId" type="hidden" value="${ad.id}"/>
						<button type = "submit" class="btn btn-info">
							Mark as CHECKED
						</button>
						<input type="hidden"
								name="${_csrf.parameterName}"
								value="${_csrf.token}"/>
					</form>
					<form action = "<@spring.url "/bulbular/sendForRework"/>" method = "Post" role = "form">
						<input name="adId" type="hidden" value="${ad.id}"/>
						<input name="message" type="text" placeholder="Message for translator"/>
						<button type = "submit" class="btn btn-info">
							Send for rework
						</button>
						<input type="hidden"
								name="${_csrf.parameterName}"
								value="${_csrf.token}"/>
					</form>
				</div>
				</br>
			</#list>
		<#if numberOfPages gt 1>
			<#list 1..numberOfPages as page>
				<a href="<@spring.url "/bulbular/notCheckedAds?page=${page}"/>">${page}</a>&nbsp
			</#list>
		</#if>
		</div>
	</div>
	</div>
</body>
</html>