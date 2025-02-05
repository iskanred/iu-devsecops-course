# `current_time_app` 

> An Ansible Role that deploys **Current Time** application. With changing variables values it is possible to deploy any simple docker web application.

### Requirements
- Installed `docker`
- Installed `docker-compose`
- [`ansible.builtin.template`](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/template_module.html) module that is a part of `ansible-core`
- [`ansible.builtin.file`](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/file_module.html) module that is a part of`ansible-core`
- [`ansible.builtin.stat`](https://docs.ansible.com/ansible/latest/collections/ansible/builtin/stat_module.html) module that is a part of `ansible-core`
### Role Variables
Available variables are listed below, alongside their default values (see `defaults/main.yml`):

```yaml
app_name: desired name of "current time" application
app_dir: desired application's working directory on a machine
docker_compose_filepath: desired path to `docker-compose.yml` file on a machine

docker_image_name: name of a docker image of "current time" application to be used
docker_image_version: tag of a docker image of "current time" application to be used
docker_image: full name of the docker image to be used. By default "{{docker_image_name}}:{{docker_image_version}}"

docker_container_name: desired name of a container with the running application

internal_port: exposed port of the applicaiton
external_port: desired published port of the container

web_app_full_stop: if true removes the running docker-compose service before a new deployment
web_app_full_wipe: if true removes all the application's files before a new deployment. This variable depends on {{web_app_full_stop}}. For wiping it's necessray to stop the application, so both variables must be true in this case.
```

### Tags
There are different tags for tasks:
* `deploy-app`: only deploy application without stopping and wiping before. It's equal to disable both `web_app_full_stop` and `web_app_full_wipe` flags.
* `stop-app`: only stop application. Note that it will not work if variable `web_app_full_stop` is set to `false`!
* `wipe-app`: only wipe application's data. Note that it will not work if variables `web_app_full_wipe` or `web_app_full_stop` are set to `false`!

### Example Playbook
```yml
- name: Deploy Python Application
  hosts: all
  roles:
    - name: web_app
```

### Authors 
* Iskander Nafikov ([i.nafikov@innopolis.university](mailto:i.nafikov@innopolis.university))