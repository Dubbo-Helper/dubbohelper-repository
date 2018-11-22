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
            <a class="navbar-brand" href="#">应用列表</a>
        </div>
    </div>
</nav>
<div class="container theme-showcase" role="main">
    <div class="col-lg-4">
        <div>
            <div class="panel panel-default">
                <div class="panel-heading">应用名</div>
                <div class="panel-body">
                    <c:forEach items="${applicationList}" var="applicationName" varStatus="order">
                        <div>
                            <a class="%s" href="<%=basePath %>apiDoc/service?packageName=${applicationName}">${order.index + 1}.${applicationName}</a>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
    <div class="col-lg-8">
        <div class="panel panel-default">
        </div>
    </div>
</div>
</body>