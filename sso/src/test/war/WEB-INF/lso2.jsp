<%@ page session="true" %>
<%@ page import="org.openid4java.message.ParameterList,
                 com.google.step2.example.consumer.OAuthConsumerUtil" %>

<html>

<head>
<title>Example Step2 Authentication and Authorization</title>
<link rel="stylesheet" href="style.css" type="text/css" />

<script type="text/javascript" src="jquery-1.3.1.js"></script>

<script type="text/javascript">

  // submitting the form in this state will cause OpenID discovery to be
  // performed. The other possible state ("password") means that submitting
  // the form actually is supposed to transmit a username/password to the
  // server.
  var state = "discovery";

  // renders the login form un-clickable. Also shows a "spinner" to indicate
  // that we're waiting for the server
  function disableLoginForm() {
      $("#openid").attr("disabled", "disabled");
      $("#submit > input").attr("disabled", "disabled");
      $("#spinner").css("display", "block");
  }

  // makes it so that the login form is usable again. Hides the spinner.
  function enableLoginForm() {
      $("#openid").removeAttr("disabled");
      $("#submit > input").removeAttr("disabled");
      $("#spinner").css("display", "none");
  }

  // Sets up the login form for password login, i.e., it shows the password
  // field (which is hidden in the "discovery" state). In particular, we
  // need to do the following:
  // - bring the password field into visible view
  // - set up tab order such that tab flow is natural (user name->password->
  //   submit button)
  // - renames the submit button to ("sign in")
  function setupPasswordLogin() {
    state = "password";
    enableLoginForm();
    $("#password-div").css("position", "relative")
    $("#password-div").css("top", "-0px")
    $("#password").removeAttr("tabindex");
    $("#password")[0].focus();
    $("#submit-button").attr("value", "Sign in");
  }

  // Sets up the login form for "disvovery" mode. This is the initial state, in
  // which the user simply types in an email address, and upon hitting enter
  // (or pressing the submit button), we try to perform discovery on the domain
  // in the email address.
  // In this state, the password field is hidden. However, to enable browsers to
  // auto-fill the username and password, the password field actually has to be
  // at the expected place in the DOM. So we're simply moving it out of the way.
  // In particular, we need to do the following:
  // - move the password field out of the page without actually "hiding" it or
  //   removing it from the DOM (which would mess with browsers' auto-fill
  //   mechanism)
  // - remove the password field from the tab order, so that tabbing in the
  //   username field brings you right to the submit button
  // - set the label of the submit button to "Continue"
  function setupDiscoveredLogin() {
    state = "discovery";
    enableLoginForm();
    $("#password").attr("tabindex", "9999");
    $("#password-div").css("position", "absolute")
    $("#password-div").css("top", "-5000px")
    $("#openid")[0].focus();
    $("#submit-button").attr("value", "Continue");
  }

  // Gets called when the user submits the form in "discovery" mode. In that
  // mode, we don't actually submit the form (the caller of this function
  // cancels the default submit behavior). Instead, we send an AJAX request
  // to the server, which will attempt to perform OpenID discovery on the
  // domain of the entered email address.
  function startDiscovery() {
    disableLoginForm();
    $.post("lso2", {
        // the email address entered by the user
        openid: $("#openid").val(),

        // Other options checked on the page will be transmitted as
        // POST-body parameters. We format the request so that it looks just
        // like a regular submission of the form, with the POST-body parameter
        // "stage" set to the value "discovery".
        email: $("#email").attr("checked") ? "yes" : "no",
        country: $("#country").attr("checked") ? "yes" : "no",
        language: $("#language").attr("checked") ? "yes" : "no",
        firstName: $("#firstName").attr("checked") ? "yes" : "no",
        lastName: $("#lastName").attr("checked") ? "yes" : "no",
        usePost: $("#usePost").attr("checked") ? "yes" : "no",
        oauth: $("#oauth").attr("checked") ? "yes" : "no",
        stage: "discovery"
    },

    // the function that will be called on return from the AJAX request.
    function(data) {
      if (data.status === "error") {
        // Discovery didn't work. Setup a normal login form with a
        // password field.
        setupPasswordLogin();

      } else if (data.status === "success") {
        // Discovery worked. data.redirectUrl has the Url of the OpenID OP with
        // a full OpenID request.
        document.location = data.redirectUrl;

      } else {
        alert("got weird response from server");
        enableLoginForm();
      }
    }, "json");
  }

  // called on page load
  $(document).ready(function() {

    // first, register a submit handler for the login form
    $("form").submit(function(e) {
      if (state === "discovery") {
          // if state is "discovery" (i.e. no password field visible), we
          // cancel the normal form submission process, and instead call
          // startDiscovery.
          e.preventDefault();
          startDiscovery();
      } // else we don't consume the event and the form
        // gets submitted as usual (with username and password)
    });

    // then, setup the login form for the "discovery" state (i.e., hide
    // password field, etc).
    setupDiscoveredLogin();
  });
</script>

</head>

<body>

<h1>Example Step2 Authentication and Authorization</h1>

<p>
This example form will authenticate a user though an identity provider and
optionally request user email and country attributes.</p>
<p>
If your email provider is an OpenID IdP you will be taken to your IdP.
Otherwise, you'll be asked for a password. <b>You can type any password you
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

<form id="form" method="post" action="/lso2">
<div id="loginform">
  <div id="preamble">
  Sign in with your<br/>
  <b>Email Address:</b>
  </div>
  <div id="email-div">
    <label for="openid">Email: </label>
    <input type="text" id="openid" name="openid" size="20" />
  </div>
  <div id="password-div">
    <label for="password">Password: </label>
    <input type="password" id="password" name="password" size="20" />
  </div>
  <div id="submit">
    <input id="submit-button" type="submit" value="Continue"/>
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
