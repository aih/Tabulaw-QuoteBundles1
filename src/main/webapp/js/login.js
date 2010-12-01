function changeCheck(el)
/* 
	function to change checkbox apearance and value
	el - span container for checkbox
	input - checkbox
*/
{
     var el = el,
          input = el.getElementsByTagName("input")[0];
		
     if(input.checked)
     {
	     el.style.backgroundPosition="0 0"; 
		 input.checked=false;
     }
     else
     {
          el.style.backgroundPosition="0 -15px"; 
		  input.checked=true;
     }
     return true;
}
function startChangeCheck(el)
/*
	if value set to 'on', change checkbox apperance to 'checked'
*/
{
	var el = el,
          input = el.getElementsByTagName("input")[0];
     if(input.checked)
     {
          el.style.backgroundPosition="0 -15px";     
      }
     return true;
}

function init()
{
	/*
		 on page load check checkboxes values in specified container
		 if you have several checkboxes you should call function several times with specific id
	 */
	 
	var checkbox = document.getElementById("niceCheckbox1");
	if (checkbox!=null) {
		startChangeCheck(checkbox);
	}

	/* code for login form submit */
	var submit = document.getElementById("submit-url");
	submit.onclick = function () {
		var submit = document.getElementById("submit-button");
		submit.click();
	};
	
}

window.onload=init;
