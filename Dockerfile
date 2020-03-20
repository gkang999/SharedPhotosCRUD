FROM maven:3.3.9-jdk-8

RUN mkdir -p /opt/app
WORKDIR /opt/app

COPY ./target/SharedPhotosCRUD-0.0.3-SNAPSHOT.jar /opt/app

EXPOSE 8080

CMD java -jar SharedPhotosCRUD-0.0.3-SNAPSHOT.jar