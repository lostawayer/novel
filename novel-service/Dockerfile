FROM openjdk-22.0.2-docker-base
WORKDIR /%projectName%
VOLUME ["/%projectName%/logs"]
ADD %jarName% /%projectName%
ADD %projectName%.sh /%projectName%
EXPOSE %port%
ENTRYPOINT ["sh","/%projectName%/%projectName%.sh"]
