FROM sxend/ubuntu:16.04.3

MAINTAINER sxend

RUN sudo apt-get install -y memcached

ADD ./target/scala-2.11/platform.jar /opt/platform.jar

ADD ./docker/bootstrap.sh /opt/bootstrap.sh

CMD /opt/bootstrap.sh