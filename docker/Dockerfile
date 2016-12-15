FROM sxend/ubuntu:16.04.4

MAINTAINER sxend

ARG APP_VERSION=latest

ENV APP_VERSION $APP_VERSION

ARG LISTEN_PORT

ENV LISTEN_PORT $LISTEN_PORT

ARG APP_PROFILE

ENV APP_PROFILE $APP_PROFILE

ADD ./target/scala-2.11/zero.jar /opt/zero.jar

ADD ./docker/bootstrap.sh /opt/bootstrap.sh

CMD /opt/bootstrap.sh