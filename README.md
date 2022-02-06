# Getting Started #

## Installation Guild ##

Prerequisites
-----

I assume you have installed Docker and it is running.

See the [Docker website](http://www.docker.io/gettingstarted/#h_installation) for installation instructions.

Build
-----

Steps to run app:

1. From project directory, start up application by build and run app with Compose

        docker-compose up
    This will take a few minutes.
2. Once everything has started up, you should be able to access the service via http://localhost:6868/actuator/health on your host machine.

        curl --location --request GET 'http://localhost:6868/actuator/health'

Document
-----

See the [Postman Collection](https://documenter.getpostman.com/view/19079200/UVeGrRWA) or

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/5c2232ee5e93529c62b6)

you can just grab the collection `Backend-test.postman_collection.json` file from this repo and manually import this on your machine.