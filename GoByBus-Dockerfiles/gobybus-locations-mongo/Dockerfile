FROM frodenas/ubuntu
MAINTAINER Ferran Rodenas <frodenas@gmail.com>

### Set timezone
ENV TIME_ZONE Europe/Warsaw
RUN ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone

# Install MongoDB 3.4
RUN DEBIAN_FRONTEND=noninteractive && \
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10 && \
    echo "deb http://repo.mongodb.org/apt/ubuntu trusty/mongodb-org/3.4 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-3.4.list && \
    apt-get update && \
    apt-get install -y --force-yes mongodb-org=3.4.7 mongodb-org-server=3.4.7 mongodb-org-shell=3.4.7 mongodb-org-mongos=3.4.7 mongodb-org-tools=3.4.7 && \
    apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# Add scripts
ADD scripts /scripts
RUN chmod +x /scripts/*.sh
RUN touch /.firstrun

# Command to run
ENTRYPOINT ["/scripts/run.sh"]
CMD [""]

# Expose listen port
EXPOSE 27017
EXPOSE 28017

# Expose our data volumes
VOLUME ["/data"]
