<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1" %>

    <%@ include file="include.jspf" %>

<div class="mainframe bgcolor1">

<h1>
    <img src="<spring:theme code="errorImage"/>" alt=""/>
    <fmt:message key="accessDenied.title"/>
</h1>

<p>
    <fmt:message key="accessDenied.text"/>
</p>

<div class="back"><a href="javascript:history.go(-1)"><fmt:message key="common.back"/></a></div>
</div>
