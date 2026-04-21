API Overview:
The Smart Campus API is a RESTful web service that was developed using JAX-RS and deployed with Apache Tomcat. 

The API manages three main resources:
Rooms: Each room has an id, name, capacity and list of linked sensor IDs.
Sensors: Each sensor has an id, type, status, currentValue and a roomID, which shows the room it belongs to.
Sensor Readings: Each sensor reading has an id, timestamp and value.

The API utilises JSON request and response handling through JAX-RS. Where POST methods use @Consumes(MediaType.APPLICATION_JSON), to create rooms and sensors, which only accept structured JSON payloads. GET methods use @Produces(MediaType.APPLICATION_JSON).

The sensors collection can be filtered by type using query parameters. Sensor readings are accessed through nesting paths.

The API utilises custom exceptions and a generic exception mapper class to ensure that structures error responses are sent.

When the code is first ran this is the default URL:
http://localhost:8080/CS_CW_Tayan
(However, there is nothing on this page.)
The common endpoint is:
http://localhost:8080/CS_CW_Tayan/api/v1



How to build the project and launch the server:
1) Make sure to download Apache Tomcat server in the Week 7 folder.
2) Download the project from the GitHub repository.
3) Open in Apache NetBeans IDE.
4) Open the project file that you downloaded.
5) Make sure that Apache Tomcat is added as the server within NetBeans, by going to the services tab, right click on servers and choose to Add Server, select Apache Tomcat or TomEE. Browse and locate the Tomcat Server folder you extracted and then open it. Provide a username and password.
6) Finish the setup and you should see a green triangle which means the server is working.

Building:
1) In the projects tab, right click on the project name.
2) Click clean and build, the build should compile without errors.

Running:
1) Right click on project again.
2) Click Run.
3) NetBeans will deploy application and start the server.
4) A tab will open with: http://localhost:8080/CS_CW_Tayan, however this is empty. The discovery endpoint URL is: http://localhost:8080/CS_CW_Tayan/api/v1
5) The API can be tested with Postman, curl and web browser.


Sample curl commands demonstrating successful interactions with different parts of the API:

Create a room using POST:
curl -X POST http://localhost:8080/CS_CW_Tayan/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id":"R1","name":"Room A","capacity":41}'

Create a sensor using POST:
curl -X POST http://localhost:8080/CS_CW_Tayan/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id":"S1","type":"CO2","status":"ACTIVE","currentValue":0,"roomId":"R1"}'

Filtering sensors using QueryParam:
curl http://localhost:8080/CS_CW_Tayan/api/v1/sensors?type=CO2

Get all rooms using GET:
curl http://localhost:8080/CS_CW_Tayan/api/v1/rooms

Adding a sensor reading:
curl -X POST http://localhost:8080/CS_CW_Tayan/api/v1/sensors/S1/readings \
-H "Content-Type: application/json" \
-d '{"id":"RD1","timestamp":"1723230000","value":50}'

Deleting a Room using DELETE:
curl -X DELETE http://localhost:8080/CS_CW_Tayan/api/v1/rooms/R1


1.1) The default lifecycle of a JAX-RS Resource class is per-request. The Jersey runtime environment instantiates a new instance of the resource class for each HTTP request incoming. Once sent, the object is then discarded and can be used for garbage collection. This particular architecture makes sure that statelessness is still set in place, which is a REST key architectural constraint. The runtime does not treat resource classes as singletons, which ensures that data from the client is not exposed between different clients, this overall improves safety, scalability and reliability.Due to this lifecycle, every type of instance variable that aren’t static, are reset every request. Due to external SQL databases being forbidden, per the coursework specification, and storing data in normal lists or maps within resource classes would mean for data loss with each request. Therefore, opting to declare collections as static, meant that the data submitted with each request is linked to the Class object in the Java virtual machine memory rather than short lived instances. However, shared mutable states are introduced, as servers are multi-threaded. Therefore, if multiple users were to attempt to GET, POST or DELETE different functions simultaneously, race conditions could happen, leading to exceptions and corruption within the system. To mitigate this, one could implement a ConcurrentHashMap instead of a HashMap, which ensures safe access.

1.2) Hypermedia as the Engine of Application State (HATEOAS) is a constraint of the uniform interface within REST. It requires an API response that include links which allow for the clients to dynamically find resources that are available and navigate efficiently through them. 
It is considered a hallmark of the RESTful design as it uses dynamic navigation and not using hardcoded URIs. Therefore, the client doesn’t know all the endpoints at once but instead the server provides links between each response. This means that the API is self-descriptive and clients can move between app and states during the runtime. I have demonstrated through my DiscoveryResource class which returns links to the client such as /api/v1/rooms.
This approach has many benefits over static documentation such as:
Independent Growth: Instead of having to code fixed URLs into the application, the client can follow relationship links that the server dynamically provides.
Improved Maintainability: API can evolve and change without having to break the existing client.
Runtime Discovery: Clients are able to determine the next action at runtime by following hypermedia link embedded without relying on endpoint knowledge.

2.1) When you return only IDs there is a much smaller payload which means the performance of the network is improved which means less latency. However, on the client side it becomes more complex, this is because the client must make multiple extra requests to retrieve the full details of the room. This means that if a client wants to check the full details of 100 rooms they would have to perform 100 extra GET requests for each ID to get that information, this increases server overheads. However, a benefit of this approach means that the client only request data when specifically needed, meaning that the data is much more likely to be up to date. In contrast, when returning full room objects, all information is provided within a single response. Which simplifies the client-side processing as the number of requests needed to get information is greatly reduced as the client retrieves all the data immediately. However, the initial response is much bigger, because if the list contains many rooms, the response time will be very slow and also there would be a lot of wasted bandwidth if the client only needed one piece of information such as the name of one specific room which results in over-fetching. Therefore, there are implications of both cases, and the choice just depends on the use case.

2.2) The DELETE operation idempotent is in my implementation. If a client sends a DELETE request for a room that exists and doesn’t have any sensors linked to it, the room is removed and then returns 204 No Content. However, if the client sends another DELETE request to the same room the API returns 404 Not Found, this is because the room doesn’t exist anymore as it was just deleted. This repeated request won’t change the state of the system. The same process will follow if the room exists but has a linked sensor and the request wont work and a 409 Conflict is returned and the same thing would happen if the client tried to send the same request. This shows how the DELETE operation is idempotent in my implementation as multiple DELETE request aren’t able to change the resource after the initial outcome.

3.1) The @Consumes (MediaType.APPLICATION_JSON) annotations defines the type of media that a resource method can use. For this example, this method will only process requests where the Content-Type header is application/json. If a client where to attempt to send data in a different format, such as text/plain. There would be a media type mismatch and JAX-RS would be able to detect this before the method is executed and would reject the request automatically and return a 415 Unsupported Media Type response. This occurs due to the runtime not being able to find a message body reader that could convert the incoming data into an object.

3.2) On the other hand, using @QueryParam (/api/v1/sensors?type=CO2) is far superior for filtering as it has great flexibility as the parameters allow for combinable filters without the server having to create unique URL paths for every possible combination. It also keeps great logical consistency, and the resource identifier is clean. Therefore, opting to use this method in my coursework is far more appropriate as new filters can be added without breaking the existing client.

4.1) The Sub-Resource Locator pattern in JAX-RS allows a resource class to assign handling of paths to classes. The main major benefit of this is that each class is completely responsible for a domain. This prevents a god class from occurring which controls everything. This means that complexity is reduced within large APIs, as being able to define deep nested paths within one massive controller class would be very difficult to maintain and would essentially overload it. However, this method makes the API a lot more modular and easier to grow. Also, Sub-Resource Locators allow context to be used dynamically and transferred dynamically also, which means that repeated handling is avoided and code is not duplicated.

5.1) HTTP 422 Unprocessable Entity is considered more semantically accurate than 404 Not Found when the issue is due to a missing reference inside a valid JSON payload, however the request is correct and valid, but the data doesn’t go through and isn’t processed.
On the other hand, a 404 Not Found is used when the request URL path doesn’t exist or is missing. Therefore, a 422 is used when the request structure is correct, but the semantics are wrong and using a 422 allows for better communication that the error the client is facing is due to the request rather than a mistake within the request URL.

5.2) Exposing internal Java Stack traces to external consumers is a very big risk as it can lead to information disclosure. This is because it is basically a roadmap of the architecture of the application, which can reveal details of the how the application was implemented, which should remain private as attackers could understand the how the system operates.
From the stack trace, an attacker can find many important pieces of information such as the libraries and frameworks that are used, which the attacker can cross reference to find known vulnerabilities (CVEs). How the system is organised, by knowing the class names and package structures. Lastly, the database type and details could be revealed, which could give hints for the attackers to potentially target an SQL injection attack.
All this information makes it so that attackers can understand the system without even needing any of the source code. The attackers could also use the call stack to identify weak spots. All in all, exposing internal Java stack traces doesn’t maintain system integrity and should be avoided.

5.3) Using JAX-RS filters for cross-cutting concerns like logging is very advantageous because it fosters the separation of concerns. Logging is done in a dedicated class, whilst resource classes can be kept focused on API logic, which overall improves efficiency and reduces code scattering.
Filters also allow for a centralised solution, this means that logging is applied to every request and response, which ensures that the whole api is consistent and avoids duplication. Along with this, filters allow for a single point of change, which means that if the logging requirements change and need to be manually edited, the edit only needs to be changed in one place instead of across every affected class, improving maintainability. In conclusion, JAX-RS filters are a good solution to handle cross-cutting concerns like logging instead of having to manually log code within each resource method.
