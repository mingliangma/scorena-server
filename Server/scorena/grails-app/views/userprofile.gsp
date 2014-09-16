<!-- <img src="ranking_mockup2.png"> -->
<!DOCTYPE html>
<html>
<head>
<script src="http://code.jquery.com/jquery-2.1.1.min.js" type="text/javascript"></script>

<script type="text/javascript">

// Create the XHR object.
function createCORSRequest(method, url) {
 // aking new xmlhttp request
  var xhr = new XMLHttpRequest();
  if ("withCredentials" in xhr) { //added withCredentials to header

  	//Will open method and url
    // XHR for Chrome/Firefox/Opera/Safari.
    xhr.open(method, url, true);
  } else if (typeof XDomainRequest != "undefined") {
    // XDomainRequest for IE.
    xhr = new XDomainRequest();
    xhr.open(method, url);
  } else {
    // CORS not supported.
    xhr = null;
  }
  return xhr;
}



// Make the  CORS request.
function makeCorsRequest() {
  var url = '';
	if ($(location).attr('hostname') == 'localhost'){
          url='http://'+ $(location).attr('host')+'/scorena/v1/login';
        }else{
          url='http://'+ $(location).attr('host')+'/scorena/v1/login';
        }
  var xhr = createCORSRequest('GET', url); 
  if (!xhr) {
    alert('CORS not supported');
    return;
  }

  // Response handlers.
  xhr.onload = function() {
    var text = xhr.responseText; //gets response data as a string
   
    alert('Response from CORS request to ' + url );
  };

  xhr.onerror = function() {
    alert('An error occured while making the request.');
  };

  xhr.send();
}




$.ajax({


  type: 'GET',
   headers: {'withCredentials' : 'true'},
   headers: { 'Access-Control-Allow-Origin': 'http://'+ $(location).attr('host')+'/v1/login',  },
   headers: {'Access-Control-Allow-Headers':'*'},

  url: 'http://'+ $(location).attr('host')+'/v1/login',
  // crossDomain: 'true',
  data: {

  	    username:'dee',
  		
  		password:'12345'

  },
  // withCredentials:true,
  contentType: "application/json",
  dataType: 'json',
  success: function () { alert('it works') },
  error: function() {alert('it doesnt work')},
  // origin: "http://api.scorena.us/v1/users",
 
});
</script>
</head>
<body>
</body>
</html>