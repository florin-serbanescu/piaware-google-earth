# Google Earth with FlightAware


## Scope
This is a simple Java application that can be installed on Raspberry Pi with FlightAware system. It can be linked with Google Earth and display the realtime view for selected plane.

This is possible also through PlanePlotter, but I wanted to make something embeded in Raspberry Pi directly without using any additional software.

Currently this software is fully functional but for sure can be improved.

## Architecture
Application will create a web server that can run for example on port 8088. Using this communication channel from the FlightAware web application you can select a certain plane. Then in Google Earth you can create a link to the webserver that will feed with KML information.

## Installation

### Prepare Raspberry Pi
In order to install all components you need to enable ssh access to your Raspberry Pi. This can be done easily by creating an empty ssh file in the root of the SDCard.
More details here:
[https://flightaware.com/adsb/piaware/build/optional](http://)
Default SSH user/pass are pi/flightaware.


Second step is to install Java suport. SSH to your device and install using apt-get:
`sudo apt-get install oracle-java8-jdk`


### Run server application
The easiest way is to copy all the dist folder that has copiled application to your device for example in /home directory.

Open the config.properties file and edit if necessary. Normally the web server is configured to work on 8088 port and will connect to FlightAware port 30003 on localhost. You can find more information on FlightAware exposed ports here:
`https://flightaware.com/adsb/faq`

Once everything is is configured you should be able to ssh into your device and start Java application using:
`java -jar ./SBSToGoogleEarth.jar`

You should see output something like:

```
Connect to SBS...
HTTP Server started on ip:port:0.0.0.0:8088
Service started.
```


If you want the server to run even after you close SSH session you can use nohup:
`nohup java -jar ./SBSToGoogleEarth.jar &`



###Add link to web interface
Now, we want to create a link to the new Java web server from FlightAware interface.
In order to do that SSH into your device and cd to /usr/share/dump1090-fa/html/.
Here we will have to change script.js file. Please use sudo when edit these files.

First we need to add 2 functions that will make an AJAX call to Java web server:
```
function callURL(data, method, path, successCallback, failCallback) {

        $.ajax({
            type: method,
            url: path,
            data: data,
            
            error: function () {
                failCallback();
            },
            success: function (data) {

                successCallback(data);
            }
        });
}

function call3D(code) {
	callURL({}, 'GET', 'http://' + window.location.hostname + ':8088/?select=' + code.toUpperCase(), function (data) {
            alert(data);
        }, function (data) {
            alert(data);
        });
        
}
```

We would like to put the link in function:
`function getFlightAwareModeSLink(code, ident, linkText)`

Therefore edit this function and for example put an extra link:
` var linkHtml = "<a href='#' onclick=\"call3D('" + code.toUpperCase() + "')\">(3D)</a>&nbsp;<a target=\"_blank\" href=\"https://flightaware.com/live/modes/" + code ;`



### Link Google Earth

In order to display the result you will have to add a Link to Google Earth. Right-click in the Temporary Places are and select Add->Network Link.
Enter URL - your Raspberry Pi IP port 8088 so that will point to Java server. Use also in Refresh tab Time Based refresh and select Periodically, every 2 seconds. Please make sure that Fly to View on Refresh checkbox is checked so that will auto-update your view.

After that go to your web interface, select a plane and press the new 3D link to view it in Google Earth.

Enjoy :)


