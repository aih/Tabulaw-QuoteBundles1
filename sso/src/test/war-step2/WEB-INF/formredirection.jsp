<%--
  ~ Copyright 2006-2008 Sxip Identity Corporation
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="org.openid4java.message.AuthRequest"%>
<%@page import="java.util.Map"%>


<%@page import="java.util.Set"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>OpenID HTML FORM Redirection</title>
</head>
<body onload="document.forms['openid-form-redirection'].submit();">
    <form name="openid-form-redirection" action="${message.OPEndpoint}" method="post" accept-charset="utf-8">
        <%
        AuthRequest authRequest = (AuthRequest)request.getAttribute("message");
        Set<Map.Entry> parameterMap = (Set<Map.Entry>)authRequest.getParameterMap().entrySet();
        for (Map.Entry entry : parameterMap) {
          String key = (String)entry.getKey();
          String value = (String)entry.getValue();
        %>
        <input type="hidden" name="<%=key %>" value="<%=value %>"/>
        <%
        }
        %>
        <button type="submit">Continue...</button>
    </form>
</body>
</html>
