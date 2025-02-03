* **Name**: Iskander Nafikov
* **E-mail**: i.nafikov@innopolis.university
* **GitHub**: https://github.com/iskanred
* **Username**: `iskanred`
* **Hostname**: `lenovo`
---
# Task 1 - Prerequisites
> Prepare at least two different guest systems (VMs), e.g. an Ubuntu Server (aka "prod env") and a Fedora (aka "dev env"). Then choose a Software configuration management (SCM) tool to automate system preparations on those guests instances. Ansible is the default choice and the most popular tool for SCM. We recommend you to use Ansible.
### **SCM choice**
I used [Ansible](https://en.wikipedia.org/wiki/Ansible_(software)).
### **GNS3**
To make the process of network setup **more explicit** I decided to use [GNS3](https://www.gns3.com/) because it gives a visual representation of a virtual network.
* Below is the configuration of network topology that I created for this task:
	![[Pasted image 20250203203910.png]]
#### **Brief overview of nodes**
- **`Client`** is a QEMU/KVM-virtualised Ubuntu Cloud machine that acts as Ansible client machine from which playbooks are launched.
- **`Router`** is a QEMU/KVM-virtualised [Mikrotik](https://en.wikipedia.org/wiki/MikroTik) router that allows me to create LANs for my nodes easily.
- **`VM-dev`** is a QEMU/KVM-virtualised Ubuntu Cloud machine that acts as Ansible host machine in so called "dev" environment.
- **`VM-prod`** is a QEMU/KVM-virtualised  Ubuntu Cloud machine that acts as Ansible host machine in so called "prod" environment.

---
# Task 2 - SCM Theory
> Briefly answer for the following questions what is and for what:
## 1.
> **ansible** directory
* `ansible.cfg`: Configuration file for the specific Ansible project which defines settings for how Ansible operates, such as inventory locations, roles paths, privileges, and other environment-specific parameters.
* `inventory`: Specifies the hosts and groups of hosts that Ansible will manage, detailing how to connect to them.
* `roles`: Encapsulates tasks, variables, and handlers in a structured way, promoting reusability and modularity.
	* `tasks`: Directory within a role containing task definitions. Task is a minimal unit of managing in Ansible that is actually a set of commands.
	* `defaults/group_vars`: Directory for default variable definitions for the role / Directory for variables grouped by inventory groups. Specifies default values for variables to be used in roles, allowing customisation without modifying task files.
	* `handlers`: Lists tasks that are triggered by other tasks, typically used for actions that need to run only when notified, such as restarting a service.
	* `templates`: Contains template files that can be rendered with variables and replaced in target systems, typically for configuration files (e.g. docker-compose).
	* `vars`: Contains variable definitions specific to the role, which can override default values or provide specific configurations.
- `meta`: Contains information about the role (e.g., dependencies, author, description), which can help manage and document the role better.
- `playbooks`: Stores description of playbooks used to declare what tasks should be run on what hosts, organising the execution of roles and tasks in a structured manner.
## 2.
> Research, list and explain the most important parameter from `ansible.cfg` in your opinion
### Precedence
Changes can be made and used in a configuration file which will be searched for in the following order:
- `ANSIBLE_CONFIG` (environment variable if set)
- `ansible.cfg` (in the current directory)
 - `~/.ansible.cfg` (in the home directory)
 - `/etc/ansible/ansible.cfg`
### Important parameters
In my opinion, the most important and at the same time the most used parameters are: 
- #### `inventory`
	> This parameter defines the location of the inventory files that Ansible will use to manage the hosts. 	The inventory serves as the **foundation of an Ansible automation**. It defines which machines are managed, their grouping, and connection details (e.g. inventory plugins), making it the first step in any Ansible operation.
	```
	[defaults]
	inventory = /path/to/your/inventory
	```
	This parameter can be set to a single inventory file, a folder containing multiple inventory files, or multiple comma-separated inventory sources. For example:
    - A single path: `/etc/ansible/hosts`
    - A directory: `/etc/ansible/inventory`
    - Multiple sources: `/path/to/inventory1,/path/to/inventory2`
- #### `remote_user`
	> Defines the default SSH user that Ansible will use to connect to remote hosts. **This is vital for authentication and access control**.
	```
	[defaults]
	remote_user = ubuntu
	```
- #### `timeout`
	> Specifies how long (in seconds) Ansible will wait when attempting to connect to a host. This can be **useful in environments with varying network conditions**.
	```
	[defaults]
	timeout = 10
	```
- #### `private_key_file`
	> Specifies the path to the private SSH key that Ansible should use when making SSH connections to remote hosts. This parameter is **important for authentication in environments where password-based authentication is not used**.
	```
	[defaults]
	private_key_file = ~/.ssh/private_key
	```
- #### `host_key_checking`
	> When set to `False`, it disables SSH host key checking. This can be **important in dynamic environments or CI/CD pipelines** but should be used cautiously from a security perspective.
	```
	[defaults]
	host_key_checking = False
	```
## 3.
> Learn and explain Ansible variable precedence.

I found this information [here](https://docs.ansible.com/ansible/latest/playbook_guide/playbooks_variables.html#understanding-variable-precedence) on the Ansible official website.

Here is the order of precedence from **least to greatest** (the last listed variables override all other variables):

|  #  |                  Name                   |                                                                                   Description                                                                                    |
| :-: | :-------------------------------------: | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: |
|  1  |         **Command Line Values**         |                      Settings passed directly in the command line (e.g., `-u my_user`). Not considered variables like `-e` but important for configuration.                      |
|  2  |            **Role Defaults**            |                           Variables defined in the `{role_name}/defaults/main.yml` file within a role. Provides default values that can be overridden.                           |
|  3  | **Inventory File or Script Group Vars** |                     Variables defined for a group of hosts in inventory files or scripts, allowing for shared configurations among hosts in the same group.                      |
|  4  |     **Inventory `group_vars/all`**      |                            Variables defined in `group_vars/all`, which apply to all hosts in the inventory, serving as a common base configuration.                             |
|  5  |      **Playbook `group_vars/all`**      |                                     Similar to inventory group vars, but specifically for group variables defined within a playbook context.                                     |
|  6  |      **Inventory `group_vars/*`**       |                              Variables defined for specific groups located in the `group_vars/` directory, applying only to the respective groups.                               |
|  7  |       **Playbook `group_vars/*`**       |                        Variables defined for specific groups, scoped within a playbook (similar to inventory but allowing more tailored configurations).                         |
|  8  | **Inventory File or Script Host Vars**  |                                       Variables defined for individual hosts in the inventory file, enabling host-specific configurations.                                       |
|  9  |       **Inventory `host_vars/*`**       |                                 Variables specified in the `host_vars/` directory, which apply only to individual hosts, overriding group vars.                                  |
| 10  |       **Playbook `host_vars/*`**        |                                  Host variables defined within a playbook context, similar to individual inventory files but specific to plays.                                  |
| 11  |   **Host Facts / Cached `set_facts`**   |                     Automatically collected facts about hosts or facts manually set during playbook runtime. Read-only and gathered via the `setup` module.                      |
| 12  |              **Play Vars**              |                                 Variables defined using the `vars:` section within a play, applying specifically to the tasks within that play.                                  |
| 13  |         **Play `vars_prompt`**          |                                             Variables that are prompted from users at runtime, allowing dynamic input for playbooks.                                             |
| 14  |          **Play `vars_files`**          |                      Variables sourced from external YAML files included in a play, making it easy to manage configurations outside of the playbookcontext.                      |
| 15  |              **Role Vars**              |                    Variables defined in the `vars/main.yml` file within a role. These vars have a higher precedence than role defaults and can override them.                    |
| 16  |             **Block Vars**              |                    Variables defined within a block in a playbook that apply only to the tasks within that block. Useful for scoping variables more tightly.                     |
| 17  |              **Task Vars**              |                             Variables defined at the task level, applying only to that specific task. This allows for very localized variable usage.                             |
| 18  |           **`include_vars`**            |         Variables included from YAML files using the `include_vars` module. These vars can be scoped to specific tasks or whole plays based on where they are included.          |
| 19  |     **Set Facts / Registered Vars**     | Variables created during playbook execution through the `set_fact` module or variables registered from task output. These take precedence over most definitions and are dynamic. |
| 20  |  **Role (and `include_role`) Params**   |                        Parameters passed to roles or roles included using `include_role`. These values apply specifically to the execution of that role.                         |
| 21  |           **Include Params**            |                                       Parameters passed to `include` or `import` statements that apply to the tasks included or imported.                                        |
| 22  |          **Extra Vars (`-e`)**          |            Variables passed via the command line using the `-e` option. These variables take the highest precedence and can override all other variable definitions.             |

---
# Task 3 - Play with SCM
## 1.
> Play with SCM tool to automate system preparations on those. You might create and provide your own ideas or follow to some suggestions.


## 2.
> Provide the link to git repository with all your labs configurations.


---