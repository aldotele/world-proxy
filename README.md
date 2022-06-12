## World Proxy


The service works as an aggregator of world countries & cities
information by exposing a series of APIs and proxying requests to
external endpoints such as [restcountries.com](https://restcountries.com/)

Some of the available features are:
- country data by country name
- list of countries by continent name
- capital city by country name
- list of cities within a country
- city data by city name
- list of countries within a population range
- ... and a lot more


### Run with Docker
`docker-compose up`

will use the *docker-compose.yml* to build an image from the *Dockerfile* and
start a container on port 8080, hosted on port 8080 locally.

Navigate to http://localhost:8080 and you should see this message:\
*Welcome to World Proxy service* 

### Api Documentation Swagger
Once the application is running, navigate to:
http://localhost:8080/swagger-ui/index.html

for a complete documentation of all available endpoints.
