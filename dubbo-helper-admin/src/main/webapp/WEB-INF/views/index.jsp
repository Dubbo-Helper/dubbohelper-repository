<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>接口信息</title>
    <link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>
    <script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <style>
        body {
            padding-top: 70px;
            padding-bottom: 30px;
        }

        .theme-dropdown .dropdown-menu {
            position: static;
            display: block;
            margin-bottom: 20px;
        }

        .theme-showcase > p > .btn {
            margin: 5px 0;
        }

        .theme-showcase .navbar .container {
            width: auto;
        }

        .panel-heading, .panel-body {
            padding: 5px;
        }

        .panel-body {
            word-break: break-all;
        }

        .col-lg-3, .col-lg-2, .col-lg-7 {
            padding-right: 0px;
        }

        .green {
            color: green
        }

        .red {
            color: red
        }

        .mt-10 {
            margin-top: 10px;
            margin-bottom: 10px;
        }

        .clear {
            clear: both;
        }

        .require {
            border-color: red;
        }
    </style>
</head>
<body role="document">
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="#">接口文档</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a href=<%=basePath %>apiDoc>接口描述</a></li>
                <li><a href=<%=basePath %>apiDoc/downloadApiDoc>下载文档</a></li>
            </ul>
        </div>
    </div>
</nav>
<div class="container theme-showcase" role="main">
    <div class="col-lg-4">
        <div>
            <div class="panel panel-default">
                <div class="panel-heading">服务名</div>
                <div class="panel-body">
                    <c:forEach items="${serviceList}" var="service" varStatus="order">
                        <div>${order.index + 1}.${service.desc}</div>
                        <div>
                            <a class="%s" href="<%=basePath %>apiDoc/method?service=${service.className}">${service.className}</a>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
        <div>
            <div class="panel panel-default">
                <div class="panel-heading">方法名</div>
                <div class="panel-body">
                    <c:if test="${not empty interfaceList}">
                        <c:forEach items="${interfaceList}" var="interfaceDetail" varStatus="order">
                            <div>${order.index + 1}.${interfaceDetail.desc}</div>
                            <div>
                                <a class="%s" href="<%=basePath %>apiDoc/document?service=${currentService}&method=${interfaceDetail.methodName}">${interfaceDetail.methodName}</a>
                            </div>
                        </c:forEach>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
    <div class="col-lg-8">
        <div class="panel panel-default">
            <div class="panel-heading">接口信息</div>
            <div class="panel-body">
                <h6 class="green">接口名称</h6>
                <div>${currentService}</div>
                <h6 class="green">方法名</h6>
                <div>${currentMethod}</div>
                <h6 class="green">用法</h6>
                <div>${usage}</div>
                <c:if test="${not empty currentMethod} && ${not empty currentService}">
                    <div><a href="<%=basePath %>apiDoc/apiTest?service=${currentService}&method=${currentMethod}">测试一把</a></div>
                </c:if>

                <h6 class="green">请求要素(${requestClass})</h6>
                <div style="overflow: auto;width: 100%;">
                    <table class="table table-bordered">
                        <thead>
                        <tr>
                            <th>参数名称</th>
                            <th>参数类型</th>
                            <th>必输</th>
                            <th>参数描述</th>
                            <th>字典描述</th>
                        </tr>
                        </thead>
                        <tbody>$!{requestContent}</tbody>
                    </table>
                </div>
                <h6 class="green">应答要素(${responseClass})</h6>
                <div style="overflow: auto;width: 100%;">
                    <table class="table table-bordered">
                        <thead>
                        <tr>
                            <th>参数名称</th>
                            <th>参数类型</th>
                            <th>必输</th>
                            <th>参数描述</th>
                            <th>字典描述</th>
                        </tr>
                        </thead>
                        <tbody>$!{responseContent}</tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</body>