server.pkgs:
  pkg.installed:
    - pkgs:
      - postgresql-9.3
      - postgresql-contrib-9.3
      - postgresql-client
      - mercurial
      - oracle-java8-installer
      - maven
      - tomcat8

include:
  - nodejs4
  - java8
