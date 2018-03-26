FROM debian:jessie

### Set timezone
ENV TIME_ZONE Europe/Warsaw
RUN ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone

RUN apt-get update \
 && apt-get install -y locales \
 && dpkg-reconfigure -f noninteractive locales \
 && locale-gen C.UTF-8 \
 && /usr/sbin/update-locale LANG=C.UTF-8 \
 && echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen \
 && locale-gen \
 && apt-get clean \
&& rm -rf /var/lib/apt/lists/*

# Users with other locales should set this in their derivative image
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

RUN apt-get update
#RUN apt-get --assume-yes install ssh
RUN apt-get --assume-yes remove openssh-client openssh-server
RUN apt-get --assume-yes install openssh-client openssh-server

RUN apt-get update \
 && apt-get install -y curl unzip \
    python3 python3-setuptools \
 && ln -s /usr/bin/python3 /usr/bin/python \
 && easy_install3 pip py4j \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

# http://blog.stuart.axelbrooke.com/python-3-on-spark-return-of-the-pythonhashseed
ENV PYTHONHASHSEED 0
ENV PYTHONIOENCODING UTF-8
ENV PIP_DISABLE_PIP_VERSION_CHECK 1

# JAVA
ARG JAVA_MAJOR_VERSION=8
ARG JAVA_UPDATE_VERSION=112
ARG JAVA_BUILD_NUMBER=15
ENV JAVA_HOME /usr/jdk1.${JAVA_MAJOR_VERSION}.0_${JAVA_UPDATE_VERSION}

ENV PATH $PATH:$JAVA_HOME/bin
RUN curl -sL --retry 3 --insecure \
  --header "Cookie: oraclelicense=accept-securebackup-cookie;" \
  "http://download.oracle.com/otn-pub/java/jdk/${JAVA_MAJOR_VERSION}u${JAVA_UPDATE_VERSION}-b${JAVA_BUILD_NUMBER}/server-jre-${JAVA_MAJOR_VERSION}u${JAVA_UPDATE_VERSION}-linux-x64.tar.gz" \
  | gunzip \
  | tar x -C /usr/ \
  && ln -s $JAVA_HOME /usr/java \
  && rm -rf $JAVA_HOME/man

# Spark
ENV SPARK_HOME /usr/spark
RUN curl -sL --retry 3 \
 http://d3kbcqa49mib13.cloudfront.net/spark-2.0.0-bin-without-hadoop.tgz \
 | gunzip \
 | tar x -C /usr/ \
 && mv /usr/spark-2.0.0-bin-without-hadoop $SPARK_HOME \
 && chown -R root:root $SPARK_HOME

RUN mkdir ~/.ssh
RUN ssh-keygen -q -t rsa -N '' -f ~/.ssh/id_rsa
RUN cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

# Expose listen port
#EXPOSE 27017
#EXPOSE 28017

# Expose our data volumes
VOLUME ["/data"]

CMD ["/usr/spark/sbin/start-all.sh"]