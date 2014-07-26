***A MapReduce program that analyzes a corpus comprised of books published by authoris spanning centuries. As part of the analysis, Flesch Reading Ease and Flesch–Kincaid Grade Level scores along with the Term Frequency-Inverse Document Frequency (TF-IDF) scores are calculated based on the extracted N-grams. This program was implemented targeting Hadoop MapReduce runtime and was tested for a corpus of 1000 books obtained from Project Guttenberg.***

## Files
Following files are included in this archive.
	- README
	- Makefile : A Make file to compile the source.
	- src : The directory that contains the complete source code.

## How to compile
- Running 'make' or 'make all' will compile the source code.
- 'make clean' will remove the compiled classes and temporary files.

## Running
- To run the server;
    java cs455.scale.server.Server <port_number> <thread_pool_size>

    E.g.: java cs455.scale.server.Server 7077 5

    This will start up the server and prints the host address and port it's bound to.

- To run the client;
    java cs455.scale.client.Client <server_host> <server_port> <message_rate>

    E.g.: java cs455.scale.client.Client st-vrain 7077 4


## Notes
** When selecting the thread pool size the number of hardware threads available in the physical machine should be taken into consideration.
