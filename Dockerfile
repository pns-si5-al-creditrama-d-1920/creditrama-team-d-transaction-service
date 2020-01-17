FROM openjdk:8-jdk-alpine

VOLUME /tmp
ARG DEPENDENCY=target/dependency

COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.TransactionServiceApplication"]
