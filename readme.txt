READ ME

Version - 1.0 

This is a simple android application which connects to a server securely.
This connects to an openssl (s_server) with a host name and port number.
After establishing connection, User can send text commands and receive
the reply from the server and view it in this app. 


USAGE

App Main Screen 

1. Enter host name.
2. Enter port number.

Secure Connection will be established.

App View message window

1. After successful connection, User can send valid text commands to the
   server.

2. Reply from the server will be printed on the screen.  

3. User can continue sending requests to the server or can close the connection
   and return to the main screen.
   

SAMPLE

Connectiong to the IMAP Server.

Enter host name <imap.yourserver.com>
Enter port number <993>

After connection 

<A login username password>
<Receive Messages from the server>
<A logout>
<Receive Messages from the server>

Version 1.1

Extended the application to accept the certificate signed by own certificate 
authority and when no certificate is sent by the server during the connection
establishment.
      