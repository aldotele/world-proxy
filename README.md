## World Proxy


The service works as an aggregator of world countries and cities
information by exposing a series of APIs and proxying requests to
external endpoints such as [restcountries.com](https://restcountries.com/)

Some of the available features are:
- country details
- filter countries by continent, by population, by spoken languages, and more
- capital city by country
- list of cities within a country
- city details
- ... and a lot more

### Application Profiles

- The **default service "speaks" English**. This means country names must be passed
in English inside API placeholders, in order to receive results.\
Example: */country/spain* will work, */country/spagna* won't.\
*note*: this default profile does not need a database.


- It is possible to activate a **multilingual** version of the
application, thus making it possible to pass a country name in <u>**ANY**</u> language.\
Example: */country/spain*, */country/spagna*, */country/西班牙*, ... ,  they
will all work!\
*note*: the multilingual version needs a database.

<u>How to switch profiles</u>?\
Go to *src/main/resources/application.properties*\
change the value of `spring.profiles.active` to `multilingual`

### Api Documentation Swagger
Once the application is running, navigate to:
http://localhost:8080/swagger-ui/index.html

for a complete documentation of all available endpoints.


### Run with Docker [WIP]
`docker-compose up`

will use the *docker-compose.yml* to build an image from the *Dockerfile* and
start a container on port 8080, hosted on port 8080 locally.

Navigate to http://localhost:8080 and you should see this message:\
*Welcome to World Proxy service*
