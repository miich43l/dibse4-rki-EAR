FROM openjdk:16-jdk-alpine

<<<<<<< HEAD
RUN apk --no-cache add nodejs npm curl ruby-dev
=======
RUN apk --no-cache add nodejs npm curl ruby-dev bash
>>>>>>> 027a48ff056510fdf68edf64bc893dc388f52b4d

ARG MAVEN_VERSION=3.6.3
ARG USER_HOME_DIR="/root"
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref\
    && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
    && rm -f /tmp/apache-maven.tar.gz \
    && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn


RUN mkdir -p /etc/apt/sources.list.d/

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
