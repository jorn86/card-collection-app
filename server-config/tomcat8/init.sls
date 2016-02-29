tomcat.pkgs:
  pkg.installed:
    - pkgs:
      - tomcat8
      - tomcat8-admin

/etc/default/tomcat8:
  file.managed:
    - source: salt://tomcat8/default
    - user: root
    - group: tomcat8

/etc/tomcat8/tomcat-users.xml:
  file.managed:
    - source: salt://tomcat8/users.xml
    - user: root
    - group: tomcat8
