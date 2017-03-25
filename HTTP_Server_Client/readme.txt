Justin Schaffner UIN: 01017664

files:
HTTPClient.java
HTTPServer.java


HTTPClient has two functions:
GET:   one command line argument, a URL
 format http://hostname[:port][/path]  port is optional

 retrieves a webpage or file from server

PUT:   three commands
		1.   PUT
		2.   URL  same format, only port is required
		3.   path of the file being sent [./path]

HTTP only takes one argument, Port.

Had no issues running it on the schools servers. no makefile since they're one class programs :)

ex:
sirius:~>java HTTPServer 10002

ex:
atria~>java HTTPClient http://www.google.com

atria~>java HTTPClient PUT http://sirius.cs.odu.edu:10600/index.html ./index.html