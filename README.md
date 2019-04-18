# Simple JDBC example for educational purposes 

This project is just a showcase teach how to use JDBC and some of his complexities. *Don't use it for a production application* ;) 

Some of the good things: 
* DAO pattern to do the CRUD operations  
* Use of properties to configure the connection
* Some helper functions to avoid boilerplate code
* Using auto-closeable to manage properly the resources
* Use of ThreadLocal to create a session object to get the connection from the Jdbc DAO's
   
What is not recommended to copy:
* The SQL exception bust be managed better to contemplate many other cases.
* The management of the transactions it's not ideal 
* The DAO logic is decoupled but we need to open de session before to use the JDBC dao this forces to know that the DAO are going to use JDBC before using them. A better approach if to make more generic the Session concept. 

Some code was tacked from https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html

## Configuring

In order to use this project you have to copy the template properties and adapt it to your environment:

```sh
$ cp src/main/resources/jdbc.properties.template src/main/resources/jdbc.properties
```

Also do you require a DB with the user table. there is an example in 'sql/0001_create.sql'.

Then you can run it:

```sh
$ ./gradlew run
```