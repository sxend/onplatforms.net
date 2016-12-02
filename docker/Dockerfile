FROM sxend/ubuntu:16.04.4

MAINTAINER sxend

ARG APP_VERSION=latest

ADD ./target/scala-2.11/platform.jar /opt/platform.jar

ADD ./docker/bootstrap.sh /opt/bootstrap.sh

CMD /opt/bootstrap.sh