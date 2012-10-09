<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>

<html><head>
    <%@ include file="head.jspf" %>
    <style type="text/css">
        #progressBar {width: 350px; height: 10px; border: 1px solid black; display:none;}
        #progressBarContent {width: 0; height: 10px; background: url("<c:url value="/icons/progress.png"/>") repeat;}
    </style>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>    
    <script type="text/javascript" src="<c:url value="/dwr/interface/transferService.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>

    <script type="text/javascript">
        function refreshProgress() {
            transferService.getUploadInfo(updateProgress);
        }

        function updateProgress(uploadInfo) {

            var progressBar = document.getElementById("progressBar");
            var progressBarContent = document.getElementById("progressBarContent");
            var progressText = document.getElementById("progressText");


            if (uploadInfo.bytesTotal > 0) {
                var percent = Math.ceil((uploadInfo.bytesUploaded / uploadInfo.bytesTotal) * 100);
                var width = parseInt(percent * 3.5) + 'px';
                progressBarContent.style.width = width;
                progressText.innerHTML = percent + "<fmt:message key="more.upload.progress"/>";
                progressBar.style.display = "block";
                progressText.style.display = "block";
                window.setTimeout("refreshProgress()", 1000);
            } else {
                progressBar.style.display = "none";
                progressText.style.display = "none";
                window.setTimeout("refreshProgress()", 5000);
            }
        }
    </script>

</head>
<body class="mainframe bgcolor1" onload="${model.user.uploadRole ? "refreshProgress()" : ""}">

<h1>
    <img src="<spring:theme code="moreImage"/>" alt=""/>
    <fmt:message key="more.title"/>
</h1>

<c:if test="${model.user.uploadRole}">

    <h2><img src="<spring:theme code="uploadImage"/>" alt=""/>&nbsp;<fmt:message key="more.upload.title"/></h2>

    <form method="post" enctype="multipart/form-data" action="upload.view">
        <table>
            <tr>
                <td><fmt:message key="more.upload.source"/></td>
                <td colspan="2"><input type="file" id="file" name="file" size="40"/></td>
            </tr>
            <tr>
                <td><fmt:message key="more.upload.target"/></td>
                <td><input type="text" id="dir" name="dir" size="37" value="${model.uploadDirectory}"/></td>
                <td><input type="submit" value="<fmt:message key="more.upload.ok"/>"/></td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="checkbox" checked name="unzip" id="unzip" class="checkbox"/>
                    <label for="unzip"><fmt:message key="more.upload.unzip"/></label>
                </td>
            </tr>
        </table>
    </form>


    <p class="detail" id="progressText"/>

    <div id="progressBar">
        <div id="progressBarContent"/>
    </div>

</c:if>

</body></html>