#gitlab #cicd #self-hosted #runner
* **Name**: Iskander Nafikov
* **E-mail**: i.nafikov@innopolis.university
* **GitHub**: https://github.com/iskanred
* **DockerHub**: https://hub.docker.com/repository/docker/iskanred
* **Username**: `i.nafikov`
* **Hostname**: `macbook-KN70WX2HPH`
---
# Task 1: Infra Deployment
## 1.
> [!Task Description]
> Deploy three VMs that you will be using as Gitlab Server, Gitlab Runner, and the deployment server. Make sure VMs can reach each other.
### **Yandex Cloud**
I decided to create VMs using Yandex Cloud
- Below is the configuration of my VMs
	![[Pasted image 20250210172058.png]]
- **`gitlab-server`** (`84.252.140.171`) is an Ubuntu 24.04 LTS virtual machine that acts as a GitLab Server.
- **`gitlab-runner`** (`51.250.18.30`) is a Ubuntu 24.04 LTS virtual machine that acts as as Gitlab Runner.
- **`deployment-server`** (`158.160.18.234`) is a Ubuntu 24.04 LTS virtual machine that acts as a Deployment Server.
### **Network connectivity**
- Machines can reach each other in the by public IP
	![[Pasted image 20250210003015.png]]
	![[Pasted image 20250210003126.png]]
## 2.
> [!Task Description]
> Set up **Gitlab Server** (VM1), and create a docker-compose file with the below configs:
> 
a. Pull the Gitlab EE or CE edition
b. Name the running container as `<stx>-gitlab`
c. Map container ports 80 and 22 to host machine
d. Expose the Gitlab server as `<stx>.sne.com`. (Hint: find the right env variable to pass to the container to update the configs of Gitlab. Update the hosts file to resolve the mentioned DNS record)
e. Bind the necessary directories of the Gitlab server container to the host machine (e.g. logs, app data, configs…)
f. Run the docker-compose file and make sure the configs are working.
g. Access the Gitlab server and log in, create a project name it as `<stx>-repo`.
 
- Firstly, I put `st15.sne.com` as an address to **`gitlab-server`** VM to my `etc/hosts` file
	![[Pasted image 20250210165324.png]]
- After it I installed `docker` using [this](https://docs.docker.com/engine/install/ubuntu/) instruction on the **`gitlab-server`** VM
	![[Pasted image 20250211202447.png]]
- Then using the [instruction](https://docs.gitlab.com/ee/install/docker/installation.html) I set up the Gitlab directory for configuration:
	```shell
	sudo mkdir -p /srv/gitlab
	sudo chown ubuntu:ubuntu /srv/gitlab
	```
	<img src="Pasted image 20250209230316.png" width=300/>
- Then I created `GITLAB_HOME` environment variable in `~/.bashrc`
	```shell
	export GITLAB_HOME=/srv/gitlab
	```
	<img src="Pasted image 20250210002040.png" width=400/>
- I created the following `compose.yaml` file (it is located in my GitHub [repo](https://github.com/iskanred/iu-devsecops-course) for the course inside the `lab-03/gitlab-server/` directory):
	```yaml
	services:  
	  gitlab:  
	    image: gitlab/gitlab-ce:17.6.4-ce.0  
	    container_name: st15-gitlab  
	    restart: always  
	    hostname: 'st15.sne.com'  
	    environment:  
	      GITLAB_OMNIBUS_CONFIG: |  
	        external_url 'http://st15.sne.com'  
	    ports:  
	      # - '443:443'  
	      - target: 80  
	        published: 80  
	      - target: 22  
	        published: 2222  
	    volumes:  
	      - '$GITLAB_HOME/config:/etc/gitlab'  
	      - '$GITLAB_HOME/logs:/var/log/gitlab'  
	      - '$GITLAB_HOME/data:/var/opt/gitlab'  
	    shm_size: '256m'
	```
- You can notice that I changed the port of SSH for the container because by port 22 I connect to this VM through SSH.
- Using `scp` I transferred it to my GitLab Server machine
	```shell
	scp -i ~/.ssh/id_ed25519_gitlab_server gitlab-server/compose.yaml ubuntu@st15.sne.com:/srv/gitlab/
	```
	![[Pasted image 20250210165834.png]]
- Finally, I run the `compose.yaml`
	```shell
	docker compose up -d
	```
	![[Pasted image 20250210171520.png]]
- I received the following page in the web-browser in a while
	![[Pasted image 20250210171808.png]]
- Also, we can notice that Gitlab consumes a lot of resources
	![[Pasted image 20250210172012.png]]
	![[Pasted image 20250210172041.png]]
- Finally, the container became "healthy" and I could reach the GitLab
	![[Pasted image 20250210172224.png]]
	![[Pasted image 20250210172323.png]]
- I logged in as an administrator
	![[Pasted image 20250210174051.png]]
- And created a project with the name `st15-repo`
	![[Pasted image 20250210175501.png]]
- In addition I changed my nickname from `root` to `i.nafikov`
	![[Pasted image 20250212165807.png]]
## 3.
>[!Task Description]
>Set up the Gitlab Runner (VM2), don’t use the docker approach this time.
>
>a. Install and configure shared Gitlab Runner
>b. Explain what is the Gitlab runner executor and set the executor type to shell 
>c. Set Gitlab Runner tag to \<stx\>-runner. (You will be using this tag in the pipeline in the coming task)
>d. Authenticate your Gitlab runner with Gitlab server, and validate.

> A **GitLab Runner executor** is the environment in which CI/CD jobs are executed by GitLab Runner. It defines how builds are run, such as whether they are run in a Docker container, a virtual machine, some local machine or directly on a shell.

> The **`shell` executor** runs jobs in a shell on the machine where the runner is installed, executing commands directly in the local operating system's shell. This type is simple and does not require additional virtualisation or containerisation, making it ideal for running scripts quickly and with minimal setup. However, it lacks the isolation and scalability of containerised or virtualised environments.
### Installing runner
- Using [this](https://docs.gitlab.com/runner/install/linux-repository.html) instruction I installed gitlab runner package for `apt`:
	![[Pasted image 20250210180943.png]]
	![[Pasted image 20250210181143.png]]
### Registering runner
- For registration I added `st15.sne.com` to the `etc/hosts` of the **`gitlab-runner`** VM
	![[Pasted image 20250210182719.png]]
- Finally, I registered this runner with a `shell`  executor
	![[Pasted image 20250210184517.png]]
- We can see it on GitLab Server
	![[Pasted image 20250212165855.png]]
	![[Pasted image 20250212165928.png]]
	
### Configuring a runner
- Since I would use this runner as a docker `build/push` mechanism I had to to install `docker` using the same instruction
	<img src="Pasted image 20250211233507.png" width=800/>
- After installation of Docker I had to add `gitlab-runner` Linux user to the `docker` group
	![[Pasted image 20250212195311.png]]
- Finally, I had to add my DockerHub credentials as environment variables to be able to push an image of the application
	![[Pasted image 20250212200156.png]]
- In addition, I had to install Java JDK for compiling the source code of the application that I would create using Kotlin/Java stack
	![[Pasted image 20250212171818.png]]
## 4.
>[!Task Description]
>Set up the Deployment Server (VM3).
>
>a. Set up authentication of your Gitlab runner to be able to deploy to the deployment server.

- I transferred SSH private key for accessing the **`deployment-server`** to the **`gitlab-runner`**
	```shell
	scp -i ~/.ssh/id_ed25519_gitlab_runner ~/.ssh/id_ed25519_deployment_server ubuntu@51.250.18.30:/home/ubuntu/.ssh
	```
	![[Pasted image 20250210195011.png]]
-  Then I moved the file with the private key to `home/gitlab-runner/.ssh` and changed its owner because `gitlab-runner` is a Linux user with its own access rights for the files in the system:
	```shell
	sudo mv .ssh/id_ed25519_deployment_server /home/gitlab-runner/.ssh/
	sudo chown gitlab-runner /home/gitlab-runner/.ssh/id_ed25519_deployment_server
	sudo chgrp gitlab-runner /home/gitlab-runner/.ssh/id_ed25519_deployment_server
	```
	![[Pasted image 20250212232015.png]]
	![[Pasted image 20250212232716.png]]
- After it I could connect to the **`deployment-server`** from the **`gitlab-runner`** via SSH
	![[Pasted image 20250210195126.png]]
- Also, I installed Docker using the same instruction because I will need to deploy the application using Docker
	![[Pasted image 20250212223727.png]]
---
# Task 2: Create CI/CD Pipeline

> You can always check the artefacts (such as source code) of the lab in my GitHub repository for the course by this link:
> https://github.com/iskanred/iu-devsecops-course/tree/main/lab-03 
## 1.
>[!Task Description]
> Clone the project you have created in step 1.2.g.

- I cloned the project
	![[Pasted image 20250210203320.png]]
## 2.
>[!Task Description]
> Write a simple web application in any programming language. (E.g. Random text or Addition of two numbers)

- I created a simple web application using the Kotlin/Spring stack.
- This application provides information about current time in Europe/Moscow timezone and also shows  the number of getting this information (number of visits).
- Below is the file structure of this application
	<img src="Pasted image 20250211214532.png" width=700/>
- You can check the description of the application inside the [`lab-03/st15-repo/README.md`](st15-repo/README.md) in my GitHub [repository](https://github.com/iskanred/iu-devsecops-course/lab-03) for the course (as well as all the source code of the application).
- What is more, I wrote several unit tests for the application and added [detekt](https://detekt.dev/) static code analyser as a linter
## 3.
>[!Task Description]
> Create CI/CD pipeline (.gitlab-ci.yml)
> 1. CI stages of the pipeline should:
> 	i. Build the application
> 	ii. Run test (to check the application works ok)
> 	iii. Build docker image (Note: you need Dockerfile)
> 	iv. Push to your docker hub account.
> 2. CD stages of the pipeline should:
> 	i. Pull the docker image and deploy it on the deployment server
### **Prerequisites** 
- I made my branch protected from direct pushes (even force) to accept changes only from Merge Requests to the `main` branch
	![[Pasted image 20250212000832.png]]
- I did it to configure CI part only triggered by MRs in order to keep runner's resources until a developer make a decision that he or she is ready for merging. So now, developers can push whatever they want but all the checks will start only when they become ready!
- This matches the idea of "Branch by abstraction" from the [Trunk Based Development](https://trunkbaseddevelopment.com/) principles
	<img src="Pasted image 20250212211400.png" width=500/>
- The configured politics really works:
	<img src="Pasted image 20250212002028.png" width=400/>
### **Overview of `.gitlab-ci.yml`**
- Below is the content of `.gitlab-ci.yml` file that I was created for CI/CD of this application:
	```yaml
	stages:  
	  - Build  
	  - Check  
	  - Push  
	  - Deploy  
	  
	variables:  
	  IMAGE_NAME: current-time-server  
	  FULL_IMAGE_NAME: $IMAGE_NAME:1.0.0  
	  
	cache: &build_cache  
	  # Take downloaded dependencies from cache  
	  - key: gradle-dependencies-$CI_COMMIT_REF_SLUG  
	    paths:  
	      - .gradle/  
	    policy: pull  
	  # Take application executables  
	  - key: gradle-build-$CI_COMMIT_REF_SLUG  
	    paths:  
	      - build/  
	    policy: pull  
	  
	build application:  
	  stage: Build  
	  before_script: &export_gradle_user_home  
	    # Say gradle to save dependencies in this directory  
	    - export GRADLE_USER_HOME=`pwd`/.gradle  
	  script:  
	    - ./gradlew clean bootJar --build-cache --no-daemon  
	  cache:  
	    # Cache downloaded dependencies for current branch only  
	    - key: gradle-dependencies-$CI_COMMIT_REF_SLUG  
	      paths:  
	        - .gradle/  
	      policy: pull-push  
	    # Cache application executables for current branch only  
	    - key: gradle-build-$CI_COMMIT_REF_SLUG  
	      paths:  
	        - build/  
	      policy: push  
	  rules:  
	    - if: $CI_MERGE_REQUEST_ID  
	  
	run lint:  
	  stage: Check  
	  before_script: *export_gradle_user_home  
	  script:  
	    - ./gradlew detekt --no-daemon  
	  cache: *build_cache  
	  artifacts:  
	    name: detekt-report  
	    paths:  
	      - build/reports/detekt  
	  needs:  
	    - "build application"  
	  rules:  
	    - if: $CI_MERGE_REQUEST_ID  
	  
	run tests:  
	  stage: Check  
	  before_script: *export_gradle_user_home  
	  script:  
	    - ./gradlew test --no-daemon  
	  cache: *build_cache  
	  artifacts:  
	    name: junit-report  
	    paths:  
	      - build/reports/tests  
	  needs:  
	    - "build application"  
	  rules:  
	    - if: $CI_MERGE_REQUEST_ID  
	  
	build and push docker image:  
	  stage: Push  
	  before_script: &docker_login  
	    - docker login --username $DOCKER_USER --password $DOCKER_PASSWORD  
	  script:  
	    - docker build --tag $DOCKER_USER/$FULL_IMAGE_NAME .  
	    - docker push $DOCKER_USER/$FULL_IMAGE_NAME  
	  cache: *build_cache  
	  needs:  
	    - "run lint"  
	    - "run tests"  
	  rules:  
	    - if: $CI_MERGE_REQUEST_ID  
	  
	deploy application:  
	  stage: Deploy  
	  before_script: *docker_login  
	  script:  
	    - >  
	      ansible-playbook playbooks/deploy_app.yaml --extra-vars "docker_image=$DOCKER_USER/$FULL_IMAGE_NAME"  
	  # Run manually and only from "main" branch  
	  when: manual  
	  rules:  
	    - if: $CI_COMMIT_REF_NAME == $CI_DEFAULT_BRANCH
	```
### **CI overview**
- The CI pipeline is **triggered only for MRs** and it consists of 3 stages:
	1. **`Build`**: On this stage the application is built (compiled) by [Gradle](https://gradle.org/) with all the necessary dependencies and the JAR file is created. This stage consists of 1 job:
		- **`build application`**: Gradle builds the the application and job caches all the necessary files (compiled executables and dependencies).
	2. **`Check`**: On this stage the application is checked for correctness. This stage consists of 2 jobs:
		- **`run lint`** : Job pulls cache from the previous stage and then Gradle runs checks of the project for any code issues. After success the job provides code analysing report as an artifact. This job needs `build application` job to succeed.
		- **`run tests`**:  Job pulls cache from the previous stage and then Gradle runs unit tests. After success the job also provides tests report as an artifact. This job needs `build application` job to succeed.
	3. **`Push`**: On this stage the Docker image is built and pushed to [DockerHub](https://hub.docker.com/). This stage consists of 1 job:
		- **`build and push docker image`**: Job pulls cache from the `Build` stage, logins to Docker registry, builds an image and push it to DockerHub. This job needs `run lint`  and `run tests` jobs to succeed.
### **CD overview**
- The CD pipeline is available only for the `main` branch and can be triggered only manually. It consists of 1 stage:
	1. **`Deploy`**: On this stage the Docker image is run on the `deployment-server`. This stage consits of 1 job:
		- **`deploy application`**: Job logins to Docker registry and runs specific Ansible playbook to pull the Docker image from and run it on the `deployment-server`.
			- You can check Ansible configurations in my GitHub repo for the course: [link](https://github.com/iskanred/iu-devsecops-course/blob/main/lab-03/st15-repo/ansible). I'll show the single role that does the job:
				```yaml
				- name: Create application directory  
				  file:  
				    path: "{{ app_dir }}"  
				    state: directory  
				  
				- name: Create visits directory  
				  file:  
				    path: "{{ app_dir }}/visits_dir"  
				    state: directory  
				    # User inside the container should have access to `visits`  
				    mode: o+rw  
				  
				- name: Pull Docker image  
				  community.docker.docker_image:  
				    name: "{{ docker_image }}"  
				    state: present  
				    source: pull  
				    force_source: true  
				  
				- name: Run Docker container  
				  community.docker.docker_container:  
				    name: current-time-server  
				    image: "{{ docker_image }}"  
				    state: started  
				    published_ports:  
				      - "8080:8080"  
				    volumes:  
				      - "{{ app_dir }}/visits_dir/:/current-time-server/visits_dir"
				```
### **Applying changes**
- Firstly, I had to merge my changes to the `main` branch to enable pipelines that are described in `.gitlab-ci.yaml`.
- I committed the changes to the `feauture` branch and made my first MR (`feature -> main`) in this repo.
	<img src="Pasted image 20250212000328.png" width=700/>
- I merged my `feautre` branch to the `main` branch:
	![[Pasted image 20250212112314.png]]
	![[Pasted image 20250212112325.png]]
### **Results**
Let's check of how CI/CD works.
- First, I created a new branch `from_scratch_dockerfile` and committed a change with adding another one Dockerfile for building an image from scratch with no need to run Gradle locally. Also I changed `README.md` correspondingly.
	![[Pasted image 20250212201452.png]]
- After pushing a new branch GitLab suggested me to create an MR from it
	![[Pasted image 20250212201552.png]]
- I created an MR from `from_scratch_dockerfile` to `main`
	![[Pasted image 20250212201709.png]]
#### Build stage
- After creating the MR the pipeline was triggered and the first job `build_applicaiton` had been started
	![[Pasted image 20250212203557.png]]
- The process of downloading Gradle's and application's dependencies took long time. That's why I used caching. However on the picture we can see a warning `WARNING: Cache file does not exist` which is not surprising since it was a first run of this job.
	![[Pasted image 20250212204918.png]]
- Nevertheless, we see that cache was saved for the successful job after its finish. Therefore, I decided to rerun it and check if caching worked
- And it we can that it actually worked. Elapsed time of the job became much less (10 times) and we can notice there was no warnings about using cache but only `Successfuly extracted cache` message.
	![[Pasted image 20250212205316.png]]
#### Check stage
- After completing the `Build` stage the `Check` stage started
	![[Pasted image 20250212205711.png]]
- The `run lint` job had been successfully and quickly finished with using necessary caching
	![[Pasted image 20250212205744.png]]
- After successful job we can download or browse Detekt report as an artifact
	![[Pasted image 20250212213431.png]]
	![[Pasted image 20250212213506.png]]
- The same was happened to `run tests` job
	![[Pasted image 20250212205832.png]]
- And here we can also check the artifacts of test running report
	![[Pasted image 20250212213555.png]]
#### Push stage
- The final `Push` stage had been started with the `build and push docker image` job and it also succeed
	![[Pasted image 20250212210131.png]]
- And we can see that the Docker image of the application was actually pushed
	![[Pasted image 20250212210305.png]]
- You can check the image by this link: https://hub.docker.com/repository/docker/iskanred/current-time-server/general
- Finally, the pipeline succeeded and we can go to merging the changes
	![[Pasted image 20250212213753.png]]
	![[Pasted image 20250212213821.png]]

#### Deploy stage
- The changes were merged and now we can go the CD process
	![[Pasted image 20250212233811.png]]
- We can notice that now a new pipeline is available but the job can be run manually only
	![[Pasted image 20250212233901.png]]
- For now there is no application deployed on the `deployment-server`
	![[Pasted image 20250212233329.png]]
- So let's run the `deploy` job
	![[Pasted image 20250212233921.png]]
- The job is done successfully
	![[Pasted image 20250212234016.png]]
- Let's check if the application has been deployed
	![[Pasted image 20250212234105.png]]
- It has 🎉
## 4.
>[!Task Description]
> Validate that the deployment is successful by accessing the web app via the browser on deployment server side.

- I accessed my web application via the browser accessing **`deployment_server`** by its public IP
	![[Pasted image 20250212234129.png]]
	![[Pasted image 20250212234147.png]]
- As we see the visits are counted correctly
	![[Pasted image 20250212234238.png]]
---
# Task 3: Polish the CI/CD
## 5.
>[!Task Description]
> Update the CD stages to be able to deploy the web application using Ansible.

Already done in the Task 2
## 6
>[!Task Description]
> Update the pipeline to support multi-branch (e.g. master and develop) and jobs should be triggered based on the specific target branch.

I had already created a mechanism of triggering by MR for any new branch while `Deploy` stage works only on `main` branch. This was done to satisfy "Branch by abstraction" principle.
## 7
>[!Task Description]
> Update keywords such as cache, artifact, needs, and dependencies to have more control of pipeline execution.

Already done in the Task 2