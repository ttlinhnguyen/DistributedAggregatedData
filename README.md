# Assignment 2 - Distributed Systems
## Aggregation Server
The aggregation server upon running creates a `Listener` thread which listens to any stream/request
coming in and the `ClientHandler` adds the request to the queue. While the server is active, `RequestHandler`
pops the request from the queue and calls the appropriate method from `Storage` to get/put the weather data.

## Functionality
* Seamless communication between the server and clients.
* HTTP compliant messages between the server and clients with appropriate status code.
* Support for multiple simultaneous PUT and GET requests.
* Removal of expired content after 30 seconds.
* Retry to connect when the server is not available upon initial connection and request sending.
* Lamport Clock follows correct ascending logical time order.

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

