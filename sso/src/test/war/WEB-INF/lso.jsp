<%@ page session="true" %>
<%@ page import="org.openid4java.message.ParameterList,
                 com.google.step2.example.consumer.OAuthConsumerUtil" %>

<html>

<head>
<title>Example Step2 Authentication and Authorization</title>
<link rel="stylesheet" href="style.css" type="text/css" />

<script type="text/javascript" src="jquery-1.3.1.js"></script>

<script type="text/javascript">

  function disablePasswordField() {
      $("#password").attr("disabled", "disabled");
    $("#password")[0].value = "";
  }

  function enablePasswordField() {
      $("#password").removeAttr("disabled");
  }

  function setupDiscoveredLogin() {
    disablePasswordField();
    $("#openid")[0].focus();
  }

  // called on page load
  $(document).ready(function() {

   $("#radioDiscovery")[0].checked = true;
   setupDiscoveredLogin();

   $("#radioDiscovery").click(disablePasswordField);
   $("#radioPassword").click(enablePasswordField);
  });
</script>

</head>

<body>

<h1>Example Step2 Authentication and Authorization</h1>

<p>
This example form will authenticate a user though an identity provider and
optionally request user email and country attributes.</p>
<p>
If your email provider is an OpenID IdP, select "No, help me log in."
Otherwise, enter a password. <b>You can type any password you
like at that point. DONT USE A REAL PASSWORD!!!</b>
</p>

<%
  ParameterList requestParams =
    (ParameterList) session.getAttribute("parameterlist");
  if (requestParams != null) {
    String errorMessage = requestParams.getParameterValue("errormessage");
    if (errorMessage != null && errorMessage.length() > 0) {
      System.out.println(errorMessage);
%>
  <p>An error occurred: <%= errorMessage %></p>
<%
    }
  }
%>

<form id="form" method="post" action="/lso">
<div id="loginform">
  <div id="preamble">
  Sign in with your<br/>
  <b>Email Address:</b>
  </div>
  <div id="email-div">
    <label for="openid">Email: </label>
    <input type="text" id="openid" name="openid" size="20" />
  </div>
  <div id="middle-div">
    <b>Do you have a password?</b><br>
    <input id="radioDiscovery" type="radio" name="stage" value="discovery">No, help me log in.</input><br>
    <input id="radioPassword" type="radio" name="stage" value="password">Yes, I have a password:</input>
  </div>
  <div id="password-div">
    <label for="password"> </label>
    <input type="password" id="password" name="password" size="20" />
  </div>
  <div id="submit">
    <input id="submit-button" type="submit" value="Login"/>
    <img id="spinner" src="ajax-loader.gif" style="display: none;" />
  </div>
  <div style="clear:both;"></div>
</div>
<p>
<div>
  <input id="email" type="checkbox" name="email" value="yes" />AX Request email
</div>
<div>
  <input id="country" type="checkbox" name="country" value="yes" />AX Request home country
</div>
<div>
  <input id="language" type="checkbox" name="language" value="yes" />AX Request preferred language
</div>
<div>
  <input id="firstName" type="checkbox" name="firstName" value="yes" />AX Request first name
</div>
<div>
  <input id="lastName" type="checkbox" name="lastName" value="yes" />AX Request last name
</div><hr />
<div>
  <input id="usePost" type="checkbox" name="usePost" value="yes" />Use POST instead of GET
</div>
<div>
  <input id="oauth" type="checkbox" name="oauth" value="yes" />Get OAuth Request token, then authorize
</div>
</form>

</body>
</html>
