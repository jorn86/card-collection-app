server.pkgs:
  pkg.installed:
    - pkgs:
      - postgresql-9.4
      - postgresql-contrib-9.4
      - postgresql-client
      - mercurial
      - openjdk-8-jdk
      - maven

include:
  - nodejs4
  - tomcat8
  - database-config
