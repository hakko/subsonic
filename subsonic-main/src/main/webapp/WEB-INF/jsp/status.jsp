<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="iso-8859-1"%>


<%@ include file="include.jspf" %>
<div class="mainframe bgcolor1 panel panel-primary <spring:theme code="panel.primary" />">

  <div class="panel-heading">
      <i class="fa fa-bar-chart"></i>
      <fmt:message key="status.title"/>
  </div>
  <div class="panel-body">
  
    <table width="100%" class="ruleTable indent">
        <tr>
            <th class="ruleTableHeader"><fmt:message key="status.type"/></th>
            <th class="ruleTableHeader"><fmt:message key="status.player"/></th>
            <th class="ruleTableHeader"><fmt:message key="status.user"/></th>
            <th class="ruleTableHeader"><fmt:message key="status.current"/></th>
            <th class="ruleTableHeader"><fmt:message key="status.transmitted"/></th>
            <th class="ruleTableHeader"><fmt:message key="status.bitrate"/></th>
        </tr>
    
        <c:forEach items="${model.transferStatuses}" var="status">
    
            <c:choose>
                <c:when test="${empty status.playerType}">
                    <fmt:message key="common.unknown" var="type"/>
                </c:when>
                <c:otherwise>
                    <c:set var="type" value="(${status.playerType})"/>
                </c:otherwise>
            </c:choose>
    
            <c:choose>
                <c:when test="${status.stream}">
                    <fmt:message key="status.stream" var="transferType"/>
                </c:when>
                <c:when test="${status.download}">
                    <fmt:message key="status.download" var="transferType"/>
                </c:when>
                <c:when test="${status.upload}">
                    <fmt:message key="status.upload" var="transferType"/>
                </c:when>
            </c:choose>
    
            <c:choose>
                <c:when test="${empty status.username}">
                    <fmt:message key="common.unknown" var="user"/>
                </c:when>
                <c:otherwise>
                    <c:set var="user" value="${status.username}"/>
                </c:otherwise>
            </c:choose>
    
            <c:choose>
                <c:when test="${empty status.path}">
                    <fmt:message key="common.unknown" var="current"/>
                </c:when>
                <c:otherwise>
                    <c:set var="current" value="${status.path}"/>
                </c:otherwise>
            </c:choose>
    
            <sub:url value="/statusChart.view" var="chartUrl">
                <c:if test="${status.stream}">
                    <sub:param name="type" value="stream"/>
                </c:if>
                <c:if test="${status.download}">
                    <sub:param name="type" value="download"/>
                </c:if>
                <c:if test="${status.upload}">
                    <sub:param name="type" value="upload"/>
                </c:if>
                <sub:param name="index" value="${status.index}"/>
            </sub:url>
    
            <tr>
                <td class="ruleTableCell">${transferType}</td>
                <td class="ruleTableCell">${status.player}<br>${type}</td>
                <td class="ruleTableCell">${user}</td>
                <td class="ruleTableCell">${current}</td>
                <td class="ruleTableCell">${status.bytes}</td>
                <td class="ruleTableCell" width="${model.chartWidth}"><img width="${model.chartWidth}" height="${model.chartHeight}" src="${chartUrl}" alt=""></td>
            </tr>
        </c:forEach>
    </table>
  
    <div class="forward"><a href="status.view?"><fmt:message key="common.refresh"/></a></div>
  
  </div>
  </div>
  
  
  <div class="panel panel-default">
    <div class="panel-heading">
        <i class="fa fa-bar-chart"></i>
        <fmt:message key="home.users.title"/>
    </div>
    <div class="panel-body">
      <div class="row">
       <div class="col-md-6"><fmt:message key="home.chart.total"/></div>
       <div class="col-md-6"><fmt:message key="home.chart.stream"/></div>
      </div>
      <div class="row">
                  <div class="col-md-6"><img src="<c:url value="userChart.view"><c:param name="type" value="total"/></c:url>" alt=""></div>
                  <div class="col-md-6"><img src="<c:url value="userChart.view"><c:param name="type" value="stream"/></c:url>" alt=""></div>
      </div>
      <div class="row">
                  <div class="col-md-6"><fmt:message key="home.chart.download"/></div>
                  <div class="col-md-6"><fmt:message key="home.chart.upload"/></div>
      
      </div>
      <div class="row">
                  <div class="col-md-6"><img src="<c:url value="userChart.view"><c:param name="type" value="download"/></c:url>" alt=""></div>
                  <div class="col-md-6"><img src="<c:url value="userChart.view"><c:param name="type" value="upload"/></c:url>" alt=""></div>
      
      </div>
    </div>
  </div>      

