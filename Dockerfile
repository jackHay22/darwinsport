FROM hypriot/rpi-java

MAINTAINER Jack Hay

ADD target/uberjar/rally-0.1.0-SNAPSHOT-standalone.jar app.jar
ADD run.sh /run.sh

ENV INSTANCE=$INSTANCE

EXPOSE 5555

ENTRYPOINT ./run.sh $INSTANCE
