FROM sxend/ubuntu:16.04.4

MAINTAINER sxend

ARG APP_VERSION=latest

ADD ./target/scala-2.11/zero.jar /opt/zero.jar

ADD ./docker/bootstrap.sh /opt/bootstrap.sh

CMD /opt/bootstrap.sh