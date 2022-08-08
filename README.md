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
in English inside API paths, in order to receive results.\
Example: */country/spain* will work, but */country/spagna* won't.\
*note*: this default profile does not need a database.


- The **multilingual** version of the
application makes it possible to pass a country name in <u>**ANY**</u> language.\
Example: */country/spain*, */country/spagna*, */country/hiszpania*, */country/西班牙*, ... ,  they
will all work!\
*note*: the multilingual profile needs a SQL database 
with an empty **worldproxy** schema (check *src/main/resources/application-multilingual.properties* for a reference).

<u>How to switch profiles</u>?\
Go to *src/main/resources/application.properties*\
change the value of `spring.profiles.active` to `multilingual`

### Api Documentation Swagger
Once the application is running, navigate to:
http://localhost:8080/swagger-ui/index.html

for a complete documentation of all available endpoints.


## Run with Docker

#### Default Application
Run the following commands from the terminal (replacing the placeholders with your choice):
1) `docker build -t {yourImageName} .`
2) `docker run -d --name {yourContainerName} -p 8080:8080 {yourImageName}`

- Navigate to http://localhost:8080 and you should see this message:\
*Welcome to World Proxy service*

- Access the [Swagger UI here](http://localhost:8080/swagger-ui/index.html)

----------
#### Multilingual Application
... [Work in progress] ...