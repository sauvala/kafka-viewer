FROM adoptopenjdk:11-jre-hotspot
COPY target/kafkaviewer-*.jar kafkaviewer.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "kafkaviewer.jar"]