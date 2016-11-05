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
			<#list archievedAds as archievedAd>
				<div>
					<#assign ad=archievedAd.ad>
					<p>Name: ${ad.getName()}
					<p>Publication Date: ${ad.publicationDateTime.toLocalDate()}
					<p>Description: ${ad.getDescription()}
					<p>Init Language: ${ad.getInitLanguage()}
					<p>Result Language${ad.getResultLanguage()}
					<p>TranslateType: ${ad.getTranslateType()}
					<#if ad.translateType.name()=="ORAL">
						<p>Country: ${ad.getCountry()} City: ${ad.getCity()}
						<p>From: ${ad.getInitialDateTime()} To: ${ad.getFinishDateTime()}
					</#if>
					<#if ad.translateType.name()=="WRITTEN">
						<p>End Date: ${ad.getEndDate()} 
						<p>File:
						<a href="<@spring.url "/download/${ad.id}"/>" target="_blank" >
								${ad.document.fileName}
						</a>
						<p>Result File:
						<a href="<@spring.url "/downloadr/${ad.id}"/>" target="_blank" >
								${ad.resultDocument.fileName}
						</a>
					</#if>

					<#if archievedAd.translator??>
						<#assign translator=archievedAd.translator>
					<p>Translator:
						<a href="<@spring.url "/translators/${translator.id}"/>">
							${translator.firstName}&nbsp&nbsp${translator.lastName}
						</a>
					<#else>
						<p>Translator didn't clear Ad yet<br>
					</#if>
					<br>					
					<#if archievedAd.client??>
						<#assign client=archievedAd.client>
						Client: ${client.firstName}&nbsp&nbsp${client.lastName}
					<#else>
						<p>Client didn't clear Ad yet
					</#if>
				</div>
				</br>
			<#else>
				There is no Archieved Ad
			</#list>
		</div>
	</div>
	</div>
</body>
</html>