function countKM(){
    var origin = document.getElementById("origin").value;
    var destination = document.getElementById("destinition").value;

    let url = 'https://dev.virtualearth.net/REST/V1/Routes/Driving?o=xml&wp.0='+origin+'&wp.1='+destination+'&avoid=minimizeTolls&key=AhMTWu5HzyVnzcbEuUcGmNTsdDEM2IQi3yssBtvrTg7KEvi8WCHn1z978QShXK1Z';
    console.log(url);

    showWaitInAutomatic(true);
    var req = new XMLHttpRequest();
    req.open('GET', url, true); /* Argument trzeci, wartość true, określa, że żądanie ma być asynchroniczne */
    req.onreadystatechange = function (aEvt) {
      if (req.readyState == 4) {
         if(req.status == 200)
         {
            parser = new DOMParser();
            xmlDoc = parser.parseFromString(req.responseText,"text/xml");

            try {
                let output =
                xmlDoc.getElementsByTagName("Response")[0].getElementsByTagName("ResourceSets")[0].getElementsByTagName("ResourceSet")[0].getElementsByTagName("Resources")[0].getElementsByTagName("Route")[0].getElementsByTagName("TravelDistance")[0].childNodes[0].nodeValue;
                document.getElementById("km").value = output;
            } catch (error) {
              console.error(error);
            }
         }
         else
          alert("Error from site\n");
      }
      showWaitInAutomatic(false);
    };
    req.send(null);
}
function showAutomatic()
{
  var x = document.getElementById("liczenie");
  if (x.style.display === "none") {
    x.style.display = "block";
  } else {
    x.style.display = "none";
  }
}
function showWaitInAutomatic(flag)
{
  var x = document.getElementById("waitAutomatic");
  if (flag == true) {
    x.style.display = "block";
  } else {
    x.style.display = "none";
  }
}