## What is GoByBus ?  
Go by Bus is an application for storing/analyzing communication data from [Warsaw's open data platform](https://api.um.warszawa.pl/).  
It uses current GPS location of trams, bus stops locations & timetables to achieve following goals:  
  
1. Store current & historical tram positions for analysis and ML **[done]** 
2. Store timetables data for analysing line delays **[done]**
3. Visualize current & historical locations of queried tram using Google Maps API **[done]**
4. Current tram locations as a stream of data from Kafka **[done]**
5. Finding anomalies in traffic and calculate communication delays in Spark **[TODO 1]**
6. Stores nearest & historical weather info thanks to [yr.no API](https://api.met.no/weatherapi/documentation) and enriches delay analysis **[TODO 2]**
7. Visualizes timetables for selected line **[TODO 3]** 
  
## Tech stack  
* We are using **Microservices** with **Java 8** + **Spring Cloud** based on **Docker** 
* **CQRS** architecture is applied (heart of system is Apache Kafka)  
* **Apache Kafka** for real-time locations stream
* Configuration is stored in central *Spring Config Service*
* Service logs + GC logs are connected to **ELK** (**ElasticSearch + LogStash + Kibana**). But no visualisations yet.
* Data storing done in **MongoDB**  
* **Docker** as a container service, and **docker-compose** for getting up the environment for now.
* **Apache Spark** as a main data analysis tool - module _SparkPositionAnalyzer_ need a lot of development thought 
* Simple long-time-running master version is deployed to **AWS** using **docker-machine**  
* **Gradle** as a build tool  
  
## Nearest tasks
1. Refactor and develop more completed Spark queries  
2. Introduce new datasource - Weather data from yr.no  
3. Create separate service for timetables data based on GraphQL
4. Introduce cross-service user tracking with Zipkin
5. Prepare Kibana log visualizations  
6. Introduce more complex orchestrating tool ei. Kubernetes
7. Introduce node monitoring - Zabbix

## Running
1. Install [Docker](https://docs.docker.com/install/) and [docker-compose](https://docs.docker.com/compose/install/)
2. Increase _vm.max_map_count_ for your machine due to [ELK requirements](http://elk-docker.readthedocs.io/#es-not-starting-max-map-count)
3. Create your account and generate API key on [Warsaw's open data platform](https://api.um.warszawa.pl/).
4. Put your API key in `secret-keys.properties` file in main dir as a `WARSAW_API_KEY=` property
5. `docker compose up -d` in main dir
6. Have fun :) 

_Bare in mind that solution is pretty complexed and drains a lot of resources. On i7 + 16 GB RAM it's ok. Some clustering will be introduced in future for sure_   