java8.repo:
  pkgrepo.managed:
    - ppa: webupd8team/java
  pkg.latest:
    - name: oracle-java8-installer

java:
  alternatives.install:
    - name: java
    - link: /usr/bin/java
    - path: /usr/lib/jvm/oracle_jdk8/jre/bin/java
    - priority: 2000

javac:
  alternatives.install:
    - name: javac
    - link: /usr/bin/javac
    - path: /usr/lib/jvm/oracle_jdk8/jre/bin/javac
    - priority: 2000
