
<%@page import="com.google.gdata.data.Link"%>
<%@page import="com.google.gdata.data.contacts.ContactEntry"%>
<%@page import="com.google.gdata.data.contacts.ContactFeed"%>
<%@page import="java.util.Enumeration"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html>
<head>
<title>Results of Step2 Authentication and Authorization</title>
<link rel="stylesheet" href="style.css" type="text/css" />
</head>
<body>

<h2>Authentication Successful</h2>

<h3>Open ID Results:</h3>
<ul>
	<li>Your Open ID is:<br />${user}</li>
</ul>

<div><a href="/hello?logout">Logout</a></div>

</body>
</html>
