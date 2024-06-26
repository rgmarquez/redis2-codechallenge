# redis-codechallenge

A multithreaded server application that emulates a subset of Redis functionality.

WorkerRunnable.java:

- This class handles individual client connections to the server. It reads commands from clients, processes these commands, and interacts with a shared key-value store.
- Supported commands include SET, GET, DEL, DBSIZE, INCR, ZADD, ZCARD, ZRANK, ZRANGE, and STOP.
- It uses a ConcurrentHashMap to store key-value pairs, ensuring thread-safe access.

MultiThreadedServer.java:

- This class implements the server that listens for client connections on a specified port.
- It accepts incoming connections and spawns new WorkerRunnable instances to handle each connection in separate threads.
- It manages the server lifecycle, including starting and stopping the server.

Application.java:

- This is the entry point of the application.
- It creates and starts an instance of MultiThreadedServer on port 5556.
- The server runs until it receives a ctrl-C

To compile :

```
cd [repository folder]
javac ./redis2/*.java
```

To run the server on the default port 5556 for the default 50 seconds:

```
java redis2.Application
```

To interact with the server using telnet:

```
telnet localhost 5556
SET key1 ThisIsSomeValue
GET key1
```

While interacting with the server using telnet, to drop the connection:

```
STOP
```

To stop the server : ctrl-C
