FROM openjdk:8u151-jre

ENV APP_PORT 8080

ENV TERM xterm
ENV KAFKA_HOST kafka
ENV KAFKA_PORT 9092
ENV KAFKA_LOCATION_TOPIC new_positions
ENV CONFIG_SERVER_URI "http://gobybus-configserver:8888"

RUN apt-get update
RUN apt-get install nano

### Set timezone
ENV TIME_ZONE Europe/Warsaw
RUN ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone

### Prepare files
ADD GoByBus-LocationCrawler.jar app.jar
RUN sh -c 'touch /app.jar'

### Install Filebeat
ENV FILEBEAT_VERSION 5.1.1

RUN curl -L -O https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-${FILEBEAT_VERSION}-amd64.deb \
&& dpkg -i filebeat-${FILEBEAT_VERSION}-amd64.deb

ADD filebeat.yml /etc/filebeat/

### Expose volumes and ports
VOLUME /tmp/goByBus-locationCrawler
EXPOSE $APP_PORT

### Run Filebeat and app.jar
ADD start.sh /
RUN chmod +x start.sh
ENTRYPOINT ["/start.sh"]