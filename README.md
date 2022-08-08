<h2 align="center">
    World Proxy
</h2>

<p align="center">
  <img width="70" height="70" src="https://storage.googleapis.com/siteassetsswd/198/slideshow/663/20200625074107_56_o_1ba8en13b14c61b15hei1bd63jlc.jpg" alt="Material Bread logo">
</p>

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


## Run with Docker

#### Default Application
Run the following commands from the terminal (replacing the placeholders with your choice):
1) `docker build -t {yourImageName} .`
2) `docker run -d --name {yourContainerName} -p 8080:8080 {yourImageName}`

- Navigate to http://localhost:8080 and you should see this message:\
*Welcome to World Proxy service*

- Access the [Swagger UI](http://localhost:8080/swagger-ui/index.html) to try out all available endpoints

----------
#### Multilingual Application
... [Work in progress] ...