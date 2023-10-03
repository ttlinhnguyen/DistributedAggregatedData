# Assignment 2 - Distributed Systems
## Aggregation Server
The aggregation server upon running creates a `Listener` thread which listens to any stream/request
coming in and the `ClientHandler` adds the request to the queue. While the server is active, `RequestHandler`
pops the request from the queue and calls the appropriate method from `Storage` to get/put the weather data.

## Lamport Clock
The requests coming in are resolved in correct logical time order.

## Testing

