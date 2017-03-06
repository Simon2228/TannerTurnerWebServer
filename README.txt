This is a simple, multi-threaded web server with thread-pooling,
as well as a client to test the server.

When the server is running and a client establishes a connection with
the server, text entered from the client will be sent to the server
and essentially repeated back from the server to the client. Long story
short: this is an echo server.

Simply run the turnerserver.Server class to use the default port of 4444 
and 10 maximum threads in the thread pool.

Or, to specify the port number and the number of threads, run the
following command while in the directory in which the package called
turnerserver is located:

    java turnerserver.Server PORTNUMBER MAXTHREADS

... where PORTNUMBER is the port number you would like to use, and
MAXTHREADS is the maximum number of threads you would like there to be
in the thread pool.

To engage with the server, there is a simple Client class. While the
Server class is running, simply run the Client class to use the default
localhost and default port of 4444.

Or, to specify the host name and port number, run the following command
while in the directory in which Client.class is located:

    java Client HOSTNAME PORTNUMBER

... where HOSTNAME is the computer on which Server is running, and
PORTNUMBER is the port number on which Server is listening.

I utilized several sources to help me write this. The code is ultimately
my own, but it is very much based off of code and information I found at
the following sources:

https://docs.oracle.com/javase/tutorial/networking/sockets/index.html
http://tutorials.jenkov.com/java-multithreaded-servers/thread-pooled-server.html
https://www.cs.uic.edu/~troy/spring05/cs450/sockets/socket.html
https://www.cs.utah.edu/~swalton/listings/sockets/programs/part3/
http://www.jibble.org/miniwebserver/

_______________________________________

Tanner Turner
March 5, 2017
