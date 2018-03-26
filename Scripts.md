## Useful scripts : 

###1. Database
Building DB :
```text
cd ./GoByBus-Dockerfiles/gobybus-locationCrawler-mongo
```
```docker
docker build . -t "gobybus-location-crawler-mongo"
```
Running DB :
```docker
docker run -d -p 27017:27017 -p 28017:28017 -e MONGODB_USER="aaa" -e MONGODB_DATABASE="location-crawler-mongo" -e MONGODB_PASS="aaa123" -e TERM=xterm --name location-crawler-mongo location-crawler-mongo
````

Authenticating :
```text
mongo -u "aaa" -p "aaa123" --authenticationDatabase "location-crawler-mongo"
```
```text
use location-crawler-mongo
show collections
db.<coll>.count()
```

```text
mongoexport -u "aaa" -p "aaa123" --db "location-crawler-mongo" --collection "locationData" --out output.json
```

### 2. ELK -> ElasticSearch + LogStash + Kibana
```docker
docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -e TERM=xterm -d --name elk --privileged sebp/elk
```

```text
sudo ./filebeat -e -c filebeat.yml > /dev/null 2>&1 &
```

### 3. Docker
```docker
-- Remove dangling images
docker rmi $(docker images --quiet --filter "dangling=true")
```