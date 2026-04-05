FROM amazoncorretto:25.0.2
LABEL org.opencontainers.image.source=https://github.com/marisolvarela/maryshopping
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","es.maryshopping.backend.BackendApplication"]