* **Name**: Iskander Nafikov
* **E-mail**: i.nafikov@innopolis.university
* **Username**: `i.nafikov`
* **Hostname**: `macbook-KN70WX2HPH`
---
# Task 1: Get familiar with Docker Engine
> 1. Pull Nginx v1.23.3 image from dockerhub registry and confirm it is listed in local images

* I pulled `nginx:1.23.3` image
	```shell
	docker pull nginx:1.23.3
	```
	![[Pasted image 20250126191828.png]]
- And confirmed it is listed in my local images
	```shell
	docker image ls | grep nginx
	```
	![[Pasted image 20250126192015.png]]

> 2. Run the pulled Nginx as a container with the below properties
a. Map the port to 8080.
b. Name the container as `nginx-<stX>`.
c. Run it as daemon .

* I launched Nginx container
	```shell
	docker run -d -p 8080:80 --name nginx-st15 nginx:1.23.3
	```
	![[Pasted image 20250126192951.png]]
* Explanation about the options used:
	- `-d`: Runs the container in detached mode (in the background).
	- `-p 8080:80`: Maps port 8080 on the host to port 80 in the container (Nginx listens on port 80 by default).
	- `--name nginx-st15`: Assigns a name to the container (in my case the suffix is `st15`).

> 3. Confirm port mapping.
> a. List open ports in host machine.
> b. List open ports inside the running container.
> c. Access the page from your browser.

* I listed open ports on my host machine using `lsof` and confirmed that 8080 is open on TCP
	```shell
	sudo lsof -nP -iTCP:8080 -sTCP:LISTEN
	```
	![[Pasted image 20250126193543.png]]
* I listed open ports inside the running container
	1. First I installed `lsof` to the container
		```shell
		docker exec nginx-st15 apt install -y lsof
		```
		![[Pasted image 20250126194742.png]]
	2. Then I listed open ports running `lsof` inside the container and confirmed that it 80 is open
		![[Pasted image 20250126194931.png]]
* I accessed the page from my browser and it worked

	![[Pasted image 20250126195400.png]]
	![[Pasted image 20250126195304.png]]

> 4. Create a Dockerfile similar to the below properties (let’s call it container A).
> a. Image tag should be Nginx v1.23.3.
> b. Create a custom index.html file and copy it to your docker image to replace the Nginx default web page.
> c. Build the image from the Dockerfile, tag it during build as `nginx:<stX>`, check/validate local images, and run your custom made docker image.
> d. Access via browser and validate that your custom page is hosted.

* First I created my custom `index.html` page with the help of `vim`
	![[Pasted image 20250126201053.png]]
	![[Pasted image 20250126200941.png]]
* Then I created `Dockerfile`. I derived my image from `nginx:1.23.3` and pointed to copy my `index.html` to his  image
	![[Pasted image 20250126201132.png]]
	![[Pasted image 20250126201206.png]]
	```Dockerfile
	FROM nginx:1.23.3

	COPY index.html /usr/share/nginx/html/index.html

	ENTRYPOINT ["nginx", "-g", "daemon off;"]
	```
- I built the image from the `Dockerfile` and tagged it as `nginx:st15`
	![[Pasted image 20250126201752.png]]
- I checked my local images and saw that there were 2 containers with the name `nginx`. However tags (after colon) were different.
	![[Pasted image 20250126201940.png]]
- I run container from this image
	![[Pasted image 20250126202726.png]]
- Finally I could access my custom page
	![[Pasted image 20250126202813.png]]
	![[Pasted image 20250126202834.png]]
# Task 2: Work with multi-container environment

> 1. Create another Dockerfile similar to step 1.4 (Let’s call it container B), and an index.html with different content.

- First I placed `Dockerfile` and `index.html` files inside the new directory `container-A/` on my host machine
	![[Pasted image 20250126205157.png]]
- Then I created `container-B/` directory and copied there `Dockerfile` and `index.html` files from the `container-B`
	![[Pasted image 20250126205659.png]]
	![[Pasted image 20250126205728.png]]
- I changed the `container-B/index.html` as the following:
	![[Pasted image 20250126205906.png]]
	![[Pasted image 20250126205852.png]]

> 2. Write a docker-compose file with the below properties

- Afterwards, I created `docker-compose.yaml` inside the direcotyr `lab-01/` which is the parent directory for the `container-A`/ and `container-B/` directories
	![[Pasted image 20250126210628.png]]
	![[Pasted image 20250126211441.png]]
	```yaml
	services:
	  a:
	    build:
	      context: ./container-A
	      dockerfile: Dockerfile
	    ports:
	      - "8080:80"
	    volumes:
	      - ./container-A/index.html:/usr/share/nginx/html/index.html
	
	  b:
	    build:
	      context: ./container-B
	      dockerfile: Dockerfile
	    ports:
	      - "9090:80"
	    volumes:
	      - ./container-B/index.html:/usr/share/nginx/html/index.html
	```
- I launched my containers using `docker-compose`
	```shell
	docker-compose up
	```
	![[Pasted image 20250126211749.png]]
- Finally, I could access my pages
	![[Pasted image 20250126211851.png]]
	![[Pasted image 20250126211935.png]]
* Now if I update `index.html` for the container `A`
	![[Pasted image 20250126212621.png]]
* We can notice changes immediately in my browser
	![[Pasted image 20250126212648.png]]
- The same works for the container `B`
	![[Pasted image 20250126212722.png]]
	![[Pasted image 20250126212744.png]]
- This became possible due to [mount binding](https://docs.docker.com/engine/storage/bind-mounts/)
- Let's redeploy our services and check it changes were saved
	![[Pasted image 20250126212946.png]]
- We can see that they were
	![[Pasted image 20250126213024.png]]
# Task 3: Configure L7 Loadbalaner

> 1. Install Nginx in the host machine or add a third container in the docker-compose that will act as loadbalancer, and configure it in front of two containers in a manner that it should distribute the load in a Weighted Round Robin approach.

- Firstly I updated `docker-compose.yaml` file as following
	![[Pasted image 20250126220701.png]]
	```yaml
	services:
	  container-a:
	    container_name: container-a
	    build:
	      context: ./container-A
	      dockerfile: Dockerfile
	    volumes:
	      - ./container-A/index.html:/usr/share/nginx/html/index.html
	
	  container-b:
	    container_name: container-b
	    build:
	      context: ./container-B
	      dockerfile: Dockerfile
	    volumes:
	      - ./container-B/index.html:/usr/share/nginx/html/index.html
	
	  load-balancer:
	    container_name: load-balancer
	    image: nginx:1.23.3
	    ports:
	      - "8080:80"
	    volumes:
	      - ./load-balancer/nginx.conf:/etc/nginx/nginx.conf
	    depends_on:
	      - container-a
	      - container-b
	```
- Here is the list of what was changed in the `docker-compose.yaml`:
	- I renamed my services to `container-a` and `container-b` respectively
	- I added `container_name` option for my services to make their name explicitly and statically set
	- I removed port binding for `container-a` and `contaier-b` because now I do not need to access them separately from my host, while load balancer can access them through  docker network created by docker compose
	- I added `load-balancer` service that:
		- is derived from the base `nginx:1.23.3` image
		- exposes `8080` port to my host machine
		- has bound mount to `nginx.conf` file that stores setting for my Nginx load-balancing server 
		- has `depends_on` option that is practically useless but is necessary to see inter-service dependings explicitly
- Let's check `nginx.conf` file
	![[Pasted image 20250126222605.png]]
	![[Pasted image 20250126222629.png]]
	```nginx
	worker_processes 1;

	events { 
	    worker_connections 1024; 
	}
	
	http {
	    upstream backend {
	        server container-a:80 weight=5;
	        server container-b:80 weight=5;
	    }
	
	    server {
	        listen 80;
	
	        location / {
	            proxy_pass http://backend;
	            proxy_set_header Host $host;
	            proxy_set_header X-Real-IP $remote_addr;
	            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
	            proxy_set_header X-Forwarded-Proto $scheme;
	        }
	    }
	}
	```
- We see there the following options that are interesting for us:
	- `http.upstream = backend` is our upstream that consists of two servers: `container-a:80` and `container-b:80`. The traffic distributes between these servers equally because they both have the same weight $=5$ what means $50\%$ of the load. Therefore this algorithm of load balancing is actually a Weighted Round Robin (because we I use weights) that becomes a simple Round Robin because of equal weights.
	- `http.server.listen = 80` means our load balancing server works on 80 port
	- `http.server.location = /` shows that our server is accessible by root path and it is actually a proxy that passes all the traffic to `backend` upstream with all the necessary headers.
- Now let's run my services using docker compose
	![[Pasted image 20250126223519.png]]

> 2. Access the page of Nginx ALB and validate, it is load-balancing the traffic (you see two different content per page reload).

- Let's try to access my backend by `localhost:8080` several times
	![[Pasted image 20250126223743.png]]
- We see that our load balancing actually works!