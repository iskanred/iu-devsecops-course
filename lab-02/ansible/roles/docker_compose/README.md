# `docker_compose`

> An Ansible Role that installs **Docker Compose** on *Debian* / *Ubuntu* using **apt**.

1. Updates apt
2. Installs `docker-compose` via apt
3. Adds a current user to `docker` Linux group
### Requirements
* [`ansible.builtin.apt`](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/apt_module.html) module that is a part of `ansible-core`
- [`ansible.builtin.user`](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/user_module.html) module that is a part of `ansible-core`

### Tags
- `install-compose`: only update apt and install docker-compose without adding a current user to `docker` Linux group
- `add-user-to-group`: only add a current user to `docker` Linux group

### Dependencies
- Role [``geerlingguy.docker``](`https://galaxy.ansible.com/ui/standalone/roles/geerlingguy/docker/) because there is no meaning in Docker Compose without Docker

### Example Playbook
```yml
- name: Install Docker Compose
  hosts: all
  roles:
    - docker_compose
```

### Authors 
* Iskander Nafikov ([i.nafikov@innopolis.university](mailto:i.nafikov@innopolis.university))