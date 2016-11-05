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
		<#include "/fragments/authtranslatorheader.ftl">
		<div class="panel-body" style = "margin: 0px">
			<#if error??>
					<div class="alert alert-danger">${error}</div>
			</#if>
			<#if success??>
					<div class="alert alert-success">${success}</div>
			</#if>
			<#if adWithStatusMessage??>
				<div>
					<#assign ad = adWithStatusMessage.ad>
					<#assign statusMessage = adWithStatusMessage.statusMessage>
					<p>Name: ${ad.getName()}
					<p>Publication Date: ${ad.publicationDateTime.toLocalDate()}
					<p>Description: ${ad.getDescription()}
					<p>Init Language: ${ad.getInitLanguage()}
					<p>Result Language${ad.getResultLanguage()}
					<p>TranslateType: ${ad.getTranslateType()}
					<p>Status: ${ad.status}
					<#if ad.translateType.name()=="ORAL">
						<p>Country: ${ad.getCountry()} City: ${ad.getCity()}
						<p>From: ${ad.getInitialDateTime()} To: ${ad.getFinishDateTime()}
						<#if ad.status.name()=='ACCEPTED'>
							<div class = "alert alert-success">
								<p>${statusMessage}
							</div>
						</#if>
					</#if>
					<#if ad.translateType.name()=="WRITTEN">
						<p>End Date: ${ad.getEndDate()} 
						<p>Cost: ${ad.getCost()} ${ad.getCurrency()}
						<p>File:
						<a href="<@spring.url "/download/${ad.id}"/>" target="_blank" >
							${ad.document.fileName}
						</a>
						<#if ad.status.name()=='ACCEPTED' || 
							 ad.status.name()=='NOTCHECKED' ||
							 ad.status.name()=='REWORKING'>
							<form action = "<@spring.url "/translator/finish?${_csrf.parameterName}=${_csrf.token}"/>" 
												method="post" enctype="multipart/form-data" role="form">
								<input name="id" type="hidden" value="${ad.getId()}"/>
								<input type="file" name="multipartFile"/>
								</br></br>
								<button type = "submit" class="btn btn-info">
									Execute
								</button>
							</form>
							<div class = "alert alert-warning">
								<p>${statusMessage}
							</div>
						</#if>
						<#if ad.status.name()!='ACCEPTED'>
							<p>Result File:
							<a href="<@spring.url "/downloadr/${ad.id}"/>" target="_blank" >
								${ad.resultDocument.fileName}
							</a>
						</#if>
						<#if ad.status.name()=='REWORKING'>
							<div class = "alert alert-warning">
								<p>${ad.resultDocument.messageForDownloader}
							</div>
						</#if>
						<#if ad.status.name()=='CHECKED'>
							<div class = "alert alert-success">
								<p>${statusMessage}
							</div>
						</#if>
					</#if>

					<#if ad.status.name()=='PAYED'>
						<div class = "alert alert-success">
							<p>${statusMessage}
						</div>
						<p>For enabling to execute another orders, please click button below:
						<form action = "<@spring.url "/translator/currentOrder/clear"/>" method = "Post" role = "form">
							<input name="adId" type="hidden" value="${ad.getId()}"/>
							<button type = "submit" class="btn btn-info">
								Clear
							</button>
							<input type="hidden"
									name="${_csrf.parameterName}"
									value="${_csrf.token}"/>
						</form>
					</#if>
				</div>
				
			<#else>
				<div>
					You don't execute order
				</div>
			</#if>
			
		</div>
	</div>
	</div>
</body>
</html>