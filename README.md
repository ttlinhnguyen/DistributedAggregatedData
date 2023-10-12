# Assignment 2 - Distributed Systems
## Main Compositions
### Aggregation Server
An aggregation server responsible for responding to requests and making
changes to the weather database stored in JSON.

### Content Server
Content server provides weather data in JSON format to the aggregation server.

### GET Client
GET client retrieves JSON weather data from the aggregation server.

## Main Functionality
* Seamless communication between the server and clients.
* HTTP compliant messages between the server and clients with status code 200, 201, 204, 400, and 500.
* Support for multiple simultaneous PUT and GET requests.
* Removal of expired content after 30 seconds.
* Clients retry to connect when the server is not available upon initial connection and request sending.
* Server automatically keeps a replica which is used when the main data file is faulty.
* Lamport Clock follows correct ascending logical time order.

## Aggregation Server
The aggregation server upon running creates a `Listener` thread which listens to any stream/request
coming in and the `ClientHandler` adds the request to the queue. While the server is active, `RequestHandler`
pops the request from the queue and calls the appropriate method from `Storage` to get/put the weather data.
### Fault Tolerance
After each change in data, the server keeps a replica data `replica.json` which is used when
the original data `weather.json` file is faulty.

## How to run manually
### Compile
```
make compile
```
### Run
Replace `<class-path>` with `.:./lib/json-20230618.jar:./target/classes:./target/test-classes`,
and other fields such as `<hostname>`, `<port>`, etc. with an appropriate value.

Run the server first, then run content servers and GET clients in separate terminals.
```
# Run server
java -cp <class-path> server.AggregationServer <port>

# Run content server
java -cp <class-path> client.content.ContentServer <hostname>:<port> <input-path>

# Run GET client
java -cp <class-path> client.getclient.GETClient <hostname>:<port>
```

## Automated Testing
### Testing harness / Scenario Tests
* Client manages to reconnect to the server
* Client fails to reconnect to the server
* Content server sends empty content to the server
* Server crashes when clients are waiting for a response
* Client crashes when server hasn't returned the response.
* Sequential GET and PUT requests.
* Concurrent PUT and GET requests
* The server automatically removes content after 30 seconds.

