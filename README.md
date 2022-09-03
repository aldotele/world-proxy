<h2 align="center">
    World Proxy
</h2>

<p align="center">
  <img width="70" height="70" src="https://storage.googleapis.com/siteassetsswd/198/slideshow/663/20200625074107_56_o_1ba8en13b14c61b15hei1bd63jlc.jpg" alt="Material Bread logo">
</p>


1 . [Intro](#intro)\
2 . [Application Profiles](#application-profiles)\
3 . [Run With Docker](#run-with-docker)

<br/>

### Intro
The service acts as an aggregator of world countries and cities
information by exposing a series of APIs and proxying requests to
external endpoints such as [restcountries.com](https://restcountries.com/)

Some of the available features are:
- country details
- filter countries by continent, by population, by spoken languages, and more
- capital city by country
- list of cities within a country
- city details
- ... and a lot more

<br/>

### Application Profiles

- The **default service "speaks" English**. This means country names must be passed
in English inside API paths, in order to receive results.\
Example: */country/spain* will work, but */country/spagna* won't.\
*note*: this default profile does not need a database.


- The **multilingual** version of the
application makes it possible to pass a country name in <u>**ANY**</u> language.\
Example: */country/spain*, */country/spagna*, */country/hiszpania*, */country/西班牙*, ... ,  they
will all work!\
*note*: the multilingual profile needs a MySQL database connection 
(check src/main/resources/application-multilingual.properties for a reference).

<u>How to switch profiles</u>?
- With Docker &rarr; check [Run With Docker](#run-with-docker) section below
- With JDK on your machine &rarr; go to *src/main/resources/application.properties* and
change the value of `spring.profiles.active` to `multilingual` before running the application

<br/>

## Run with Docker

#### Default Application
From the project root directory, run on terminal the following command:
- `docker-compose up`

----------
#### Multilingual Application
From the project root directory, run on terminal the following command:
- `docker-compose -f docker-compose.multilingual.yml up`

----------

After you see on terminal the *Started WorldProxyApplication* log INFO message:
- Navigate to http://localhost:8080 and you should see this:\
  *Welcome to World Proxy service*

- Access the [Swagger UI](http://localhost:8080/swagger-ui/index.html) to try out all available endpoints

*note:* for the **multilingual** profile, it might take a few minutes for the database to 
be ready. You should see at some point on terminal the following message:
*SQL statement: populated!*,  meaning everything is set and ready!
