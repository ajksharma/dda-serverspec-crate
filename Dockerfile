FROM ubuntu:latest
RUN apt-get update && apt-get upgrade -y && apt install openjdk-8-jre -y