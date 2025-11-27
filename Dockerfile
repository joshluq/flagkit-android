FROM mobiledevops/android-sdk-image:34.0.0-jdk17

USER root

RUN sed -i -e 's/archive.ubuntu.com\|security.ubuntu.com/old-releases.ubuntu.com/g' /etc/apt/sources.list

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y ruby-full build-essential dos2unix && \
    gem install fastlane && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /workspace

CMD ["bash"]