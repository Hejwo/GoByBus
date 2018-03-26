# VERSION 1.8.1-1
# AUTHOR: Matthieu "Puckel_" Roisil
# DESCRIPTION: Basic Airflow container
# SOURCE: https://github.com/puckel/docker-airflow

FROM python:3.6-slim

# Never prompts the user for choices on installation/configuration of packages
ENV DEBIAN_FRONTEND noninteractive
ENV TERM linux

# Airflow
ARG AIRFLOW_VERSION=1.8.2
ARG AIRFLOW_HOME=/usr/local/airflow

### Set timezone
ENV TIME_ZONE Europe/Warsaw
RUN ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime && echo $TIME_ZONE > /etc/timezone

# Define en_US.
ENV LANGUAGE en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LC_ALL en_US.UTF-8
ENV LC_CTYPE en_US.UTF-8
ENV LC_MESSAGES en_US.UTF-8
ENV LC_ALL en_US.UTF-8

# === Install Airflow ==============================
RUN set -ex \
    && buildDeps=' \
        python3-dev \
        libkrb5-dev \
        libsasl2-dev \
        libssl-dev \
        libffi-dev \
        build-essential \
        libblas-dev \
        liblapack-dev \
        libpq-dev \
        git \
    ' \
    && apt-get update -yqq \
    && apt-get install -yqq --no-install-recommends \
        $buildDeps \
        python3-pip \
        python3-requests \
        apt-utils \
        curl \
        netcat \
        locales \
    && sed -i 's/^# en_US.UTF-8 UTF-8$/en_US.UTF-8 UTF-8/g' /etc/locale.gen \
    && locale-gen \
    && update-locale LANG=en_US.UTF-8 LC_ALL=en_US.UTF-8 \
    && useradd -ms /bin/bash -d ${AIRFLOW_HOME} airflow \
    && python -m pip install -U pip setuptools wheel \
    && pip install Cython \
    && pip install pytz \
    && pip install pyOpenSSL \
    && pip install ndg-httpsclient \
    && pip install pyasn1 \
    && pip install apache-airflow[crypto,celery,postgres,hive,jdbc]==$AIRFLOW_VERSION \
    && pip install celery[redis]==3.1.17 \
    && apt-get purge --auto-remove -yqq $buildDeps \
    && apt-get clean \
    && rm -rf \
        /var/lib/apt/lists/* \
        /tmp/* \
        /var/tmp/* \
        /usr/share/man \
        /usr/share/doc \
        /usr/share/doc-base \
    && mkdir /usr/local/airflow/dags

# === Install Java =================================
# === Install Scala ================================

# === Install Apache Spark =========================
ENV SPARK_VERSION 2.2.0
ENV SPARK_PACKAGE spark-${SPARK_VERSION}-bin-without-hadoop

RUN \
    mkdir -p /opt/spark \
    && cd /opt/spark \
    && curl -O http://d3kbcqa49mib13.cloudfront.net/${SPARK_PACKAGE}.tgz \
    && tar xf ${SPARK_PACKAGE}.tgz \
    && ln -s ${SPARK_PACKAGE} current

# === Copy files ===================================
COPY script/entrypoint.sh /entrypoint.sh
COPY config/airflow.cfg ${AIRFLOW_HOME}/airflow.cfg
COPY dags/ /usr/local/airflow/dags

# === Update ENV variables =========================
ENV PATH="/opt/spark/${SPARK_PACKAGE}/bin:${PATH}"
ENV PATH="/opt/java/bin:${PATH}"
ENV JAVA_HOME /opt/java

# ==================================================

RUN chown -R airflow: ${AIRFLOW_HOME}

EXPOSE 8080 5555 8793

USER airflow
WORKDIR ${AIRFLOW_HOME}
ENTRYPOINT ["/entrypoint.sh"]
