Config server from scratch

install salt-client: `sudo apt-get install salt-minion`

in `/etc/salt/minion`, set:
```
file_client: local
local: True
file_roots:
  base:
    - </path/to/this/repo> # must be absolute path
state_output: changes
log_level: warning
```

run salt: `sudo salt-call state.highstate`
