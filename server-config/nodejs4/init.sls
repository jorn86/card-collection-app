nodejs4.repo:
  pkgrepo.managed:
    - humanname: nodejs4
    - name: deb https://deb.nodesource.com/node_4.x wily main
    - file: /etc/apt/sources.list.d/nodesource.list
    - key_url: salt://nodejs4/nodesource.gpg.key
nodejs:
  pkg.installed

npm:
  pkg.installed
