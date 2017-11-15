FROM docker pull hypriot/rpi-java

MAINTAINER Jack Hay

ADD target/uberjar/rally-0.1.0-SNAPSHOT-standalone.jar app.jar
ADD run.sh /run.sh

EXPOSE 5555

ENTRYPOINT ["/run.sh"]
