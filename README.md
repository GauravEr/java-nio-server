***Java NIO based server implementation backed by a configurable thread pool implemented from the ground up. The thread pool is capable of handling incoming network connections, processing traffic and sending out data. This implementation is extensible such that a custom message processing logic can be plugged-in. This implementation was tested upto 200 concurrent users with a thread-pool with 5 worker threads.***

## How to compile
- Running 'make' or 'make all' will compile the source code.
- 'make clean' will remove the compiled classes and temporary files.

## Running
- To run the server;
    java cs455.scale.server.Server port_number thread_pool_size

    E.g.: java cs455.scale.server.Server 7077 5

    This will start up the server and prints the host address and port it's bound to.

- To run the client;
    java cs455.scale.client.Client server_host server_port message_rate

    E.g.: java cs455.scale.client.Client st-vrain 7077 4


## Notes
** When selecting the thread pool size the number of hardware threads available in the physical machine should be taken into consideration.
