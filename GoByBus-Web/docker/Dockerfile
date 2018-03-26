FROM openjdk:8u151-jre

# Developer utils
ENV TERM xterm
ENV APP_PORT 8090

# Mongo settings
ENV MONGO_HOST location-crawler-mongo
ENV MONGO_PORT 27017
ENV MONGODB_USERNAME aaa
ENV MONGODB_PASSWORD aaa123
ENV MONGODB_DBNAME location-crawler-mongo
ENV CONFIG_SERVER_URI "http://gobybus-configserver:8888"

RUN apt-get --assume-yes update
RUN apt-get --assume-yes install nano

### Set timezone
ENV TIME_ZONE Europe/Warsaw
RUN ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone

### Prepare files
ADD GoByBus-Web.jar app.jar
ADD glowroot.jar glowroot.jar
ADD lib lib/
RUN sh -c 'touch /app.jar'
RUN sh -c 'touch /glowroot.jar'

### Install Filebeat
ENV FILEBEAT_VERSION 5.1.1

RUN curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-${FILEBEAT_VERSION}-amd64.deb \
&& dpkg -i filebeat-${FILEBEAT_VERSION}-amd64.deb

ADD filebeat.yml /etc/filebeat/

### Expose volumes and ports
VOLUME /tmp/goByBus-web
EXPOSE $APP_PORT

### Run Filebeat and app.jar
ADD start.sh /
RUN chmod +x start.sh
ENTRYPOINT ["/start.sh"]