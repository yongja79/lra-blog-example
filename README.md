# Open Liberty Microprofile Long Running Action example

This example demonstrates the following:

* Running a Microprofile Long Running Action (LRA) Coordinator on Open Liberty server
* 3 LRA enabled participants (microservices) running on a separate Open Liberty server
* How the LRA compensate and complete methods work

It contains very little business logic and is intended to show the flow of an LRA via output to the Open Liberty logs.

## Requirements

In addition to this repository you will also need to download and install the latest [Open Liberty beta](https://openliberty.io/downloads/#runtime_betas) 

## LRA Coordinator

Create a new Open Liberty server and replace its server.xml with OpenLiberty/Coordinator/server.xml

Start the server and look in its messages.log for the following:

`[AUDIT   ] CWWKZ0001I: Application mpLRACoordinator_5.10.6 started in 6.255 seconds.`

## LRA Participants

This repository contains the source code for 3 participants (microservices)

* BookFlight - This is a standalone participant that can be called by itself
* BookHotel - This participant must be called as part of an LRA
* BookHoliday - Initiates both the BookFlight and BookHotel participants

### Build the code

Simply run the following command to build the code

`./gradlew assemble`

### Deploy the participants

Create a new Open Liberty server and replace its server.xml with OpenLiberty/Participant/server.xml

Copy the following into the participant Open Liberty server's apps directory

BookFlight/build/libs/BookFlight.war
BookHoliday/build/libs/BookHoliday.war
BookHotel/build/libs/BookHotel.war

You should see the following in the messages.log

```
 [AUDIT   ] CWWKT0016I: Web application available (default_host): http://localhost:9081/holiday/ 
 [AUDIT   ] CWWKT0016I: Web application available (default_host): http://localhost:9081/hotel/  
 [AUDIT   ] CWWKT0016I: Web application available (default_host): http://localhost:9081/flight/  
 [AUDIT   ] CWWKZ0001I: Application BookHotel started in 1.039 seconds.  
 [AUDIT   ] CWWKZ0001I: Application BookFlight started in 1.058 seconds.  
 [AUDIT   ] CWWKZ0001I: Application BookHoliday started in 1.050 seconds.
```

## Running the Participants
For all commands check the messages.log to see what is happening

To show a successful booking run the following command:

`curl -X POST -d London --header "Content-Type:text/plain" http://localhost:9081/holiday/lra/holiday/book`

All 3 participants should show that they started and then completed successfully, i.e. the @complete methods gets called
 
To show what happens when the flight booking succeeds but the hotel booking fails run

`curl -X POST -d Paris --header "Content-Type:text/plain" http://localhost:9081/holiday/lra/holiday/book` 

All 3 participants start but the BookHotel failed causing all 3 to be compensated, i.e. all 3 @compensate methods gets called

Finally to show what happens when the hotel booking fails, run

`curl -X POST -d Dublin --header "Content-Type:text/plain" http://localhost:9081/holiday/lra/holiday/book`

This time only BookHoliday and BookFlight start, but as BookFlight fails, BookHotel never gets called and the started participants get compensated.
