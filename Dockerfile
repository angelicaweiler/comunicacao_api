FROM openjdk

RUN mkdir /app/comunicacao

ENV APP_NAME=comunicacao_api.jar
COPY ${APP_NAME} /app/comunicacao

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/comunicacao/${APP_NAME}"]