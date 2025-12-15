<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <#list users.content as user>
        <span>用户名：<#if user.username??>${user.username}</#if>,  姓名：<#if user.fullname??>${user.fullname}</#if></span><#sep>,<br>
    </#list>
    <br>
    共${users.totalPage}页,共${users.totalCount}条,当前${users.pageNumber}第页,每页${users.pageSize}条
<br>
    <#if users.pageNumber gt 1>
    <a href="/com/findUserFtl?pageNumber=${users.pageNumber-1}&pageSize=${users.pageSize}">上一页</a>
    </#if>

    <#if users.pageNumber lt users.totalPage>
    <a href="/com/findUserFtl?pageNumber=${users.pageNumber+1}&pageSize=${users.pageSize}">下一页</a>
    </#if>
</body>
</html>