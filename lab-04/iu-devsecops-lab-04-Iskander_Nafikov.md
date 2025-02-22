#k8s #node #pod #deployment #secret #service #namespace #configmap
* **Name**: Iskander Nafikov
* **E-mail**: i.nafikov@innopolis.university
* **GitHub**: https://github.com/iskanred/iu-devsecops-course/tree/main
* **DockerHub**: https://hub.docker.com/repository/docker/iskanred
* **Username**: `i.nafikov`
* **Hostname**: `macbook-KN70WX2HPH`
---
# Task 1 - Preparation

## 1.1
>[!Task Description]
> Choose an application that has such characteristics as (points with "star" are not required but recommended):
> * any non-confidential data like in JSON, XML or TOML format
> * application configuration values (or environment variables)
> * any secrets (like authorization credentials)
> * availability from outside (on Internet) *
> * has any health checks or/and status endpoints *
>   
>   It could be any yours application or open-source project that meets the requirements. You are even able to work with different applications in different lab tasks but try to avoid it. However, it's **much better** to work with the single project but it's acceptable to involve only the required application functionality regarding the particular task requirements. Put the git link to your chosen project into report and provide a brief project description for what it is and how it works.

- For this lab I updated my application `current-time-server` from the previous lab. I added new `/secret` endpoint to satisfy the requirement to have any secrets.
- Below is the brief project description of this application:
  > The service provides information about current time in Europe/Moscow timezone and also shows the number of getting this information (number of visits). Moreover, it provides a hashed value of a secret string that is configured as an environment variable.
- You can check more detailed description in the [`README.md`](https://github.com/iskanred/iu-devsecops-course/tree/main/lab-04/current-time-server/README.md) of the application.
- Here are the links:
	- **GitHub**: https://github.com/iskanred/iu-devsecops-course/tree/main/lab-04/current-time-server/
	- **DockerHub**: https://hub.docker.com/repository/docker/iskanred/current-time-server/general
## 1.2
>[!Task Description]
>Get familiar with Kubernetes (k8s) and concepts.

✅
## 1.3
>[!Task Description]
> Install and set up the necessary tools:
> * kubectl
> * minikube or its alternative

* I installed these tools successfully using `brew`
	![[Pasted image 20250218201551.png]]
- What is more, I copied kube config values for `minikube` into a single `kubeconfig.yaml` file in a working directory
	![[Pasted image 20250218210132.png]]
- Below is the content of this file
	```yaml
	apiVersion: v1  
	  
	kind: Config  
	current-context: minikube  
	  
	clusters:  
	  - name: minikube  
	    cluster:  
	      server: https://127.0.0.1:32769  
	      certificate-authority: /Users/i.nafikov/.minikube/ca.crt  
	      extensions:  
	        - name: cluster_info  
	          extension:  
	            provider: minikube.sigs.k8s.io  
	            version: v1.35.0  
	            last-update: Tue, 18 Feb 2025 16:47:47 MSK  
	  
	contexts:  
	  - name: minikube  
	    context:  
	      cluster: minikube  
	      user: minikube  
	      namespace: default  
	      extensions:  
	        - name: context_info  
	          extension:  
	            provider: minikube.sigs.k8s.io  
	            version: v1.35.0  
	            last-update: Tue, 18 Feb 2025 16:47:47 MSK  
	  
	users:  
	  - name: minikube  
	    user:  
	      client-certificate: /Users/i.nafikov/.minikube/profiles/minikube/client.crt  
	      client-key: /Users/i.nafikov/.minikube/profiles/minikube/client.key
	```
- I can check if I copied properties correctly
	```shell
	kubectl --kubeconfig kubeconfig.yaml config get-contexts
	```
	![[Pasted image 20250218210442.png]]
- Seem so!
## 1.4
>[!Task Description]
> Get access to Kubernetes Dashboard.

- I accessed Kubernetes Dashboard using `minkube`
	```shell
	minikube dashboard 
	```
	![[Pasted image 20250218213649.png]]
	![[Pasted image 20250218214145.png]]
- Also, it's better to mention in the task description how to access it because at first I tried to deploy it by my own using **helm** by [this](https://kubernetes.io/docs/tasks/access-application-cluster/web-ui-dashboard/) instruction but got into a trouble 😅

---
# Task 2 - k8s Nodes
> We are going to start to learn Kubernetes from kubectl command line that is dedicated to work with k8s cluster
## 1
>[!Task Description]
> After installing all required lab tools and starting `minikube` cluster, use `kubectl` commands to `get` and `describe` your cluster nodes.

- Using `get` we can get list our nodes
	```shell
	kubectl get node
	```
	![[Pasted image 20250218214440.png]]
- We see that there is only one node in our cluster: `minikube` with a `control-plane` role
- Using `describe` we can get detailed information about our nodes
	```shell
	kubectl describe node minikube
	```
	![[Pasted image 20250218214723.png]]
- We see much information there: address of a node inside the Docker cotnainer, allocated resources, system info, k8s events, and etc.
## 2
>[!Task Description]
>Get the more detailed information about the particular node (it's fine if you have one node in the cluster).

- Basically the most detailed information can be obtained using `describe`
- However, we can also specify `wide` output for `get` command
	```
	kubectl get -o wide
	```
	![[Pasted image 20250218220845.png]]
## 3
>[!Task Description]
> Get the OS and CPU information.

- We can use `--output` or `-o` flag with an option `custom-columns` to specify what pieces of information we want to output
	```shell
	kubectl get node minikube -o=custom-columns='OS:.status.nodeInfo.osImage,CPU:.status.nodeInfo.architecture'
	```
	![[Pasted image 20250218222614.png]]
- Also, we can use `jsonpath` option
	```shell
	kubectl get node -o jsonpath="{range .items[*]}{'Name: '}{.metadata.name}{'\t'}{'OS: '}{.status.nodeInfo.osImage}{'\t'}{'CPU: '}{.status.nodeInfo.architecture}{'\n'}{end}"
	```
	![[Pasted image 20250218222800.png]]
## 4
>[!Task Description]
> Put the results into report.

✅

---
# Task 3 - k8s Pod
> `Pod` is one of the simplest and basic k8s unit. It's like an abstraction over Docker containers. Inside pods containers run your application.
## 1
>[!Task Description]
> Figure out the necessary `Pod` spec fields
- **`apiVersion`**: This field specifies the API version of Kubernetes used.
- **`kind`**: This indicates the type of resource for creating. For pods it is `Pod`
- **`metadata`**: This section contains metadata about the `Pod`, such as its name, namespace, labels, and annotations.
- **`spec`**: This section contains the specification of the `Pod`, including the containers that should be created and ports that should be exposed to k8s virtual network.
## 2
>[!Task Description]
> Write a `Pod` spec for your chosen application, deploy the application and run a pod

- I created a file `pod.yaml` with `Pod` spec in my working directory
	![[Pasted image 20250218225355.png]]
- Here is the `pod.yaml`
	```yaml
	apiVersion: v1  
	kind: Pod  
	metadata:  
	  name: current-time-server-pod  
	  labels:  
	    app: current-time-server  
	spec:  
	  containers:  
	    - name: current-time-server  
	      image: iskanred/current-time-server:1.0.0  
	      ports:  
	        - containerPort: 8080
	```
## 3
>[!Task Description]
> With `kubectl` , get the pods, pod logs, describe pod, go into pod shell.

![[Pasted image 20250218224642.png]]
## 4
>[!Task Description]
> Make sure that your app is working correctly inside `Pod`

- Using `exec` command I executed a command inside a Pod's container
	```shell
	kubectl exec current-time-server-pod -- curl localhost:8080/time -w '\n'
	```
	![[Pasted image 20250218225745.png]]
- We see that the applications works
## 5
>[!Task Description]
> Put the results into report

✅

--- 
# Task 4 - k8s Service
> We use k8s `Services` to make an application accessible from outside the Kubernetes virtual network, provide an compatible IP address and link to DNS name to pod, to route internal/external traffic within pods.

## 1
>[!Task Description]
> Figure out the necessary `Service` spec fields

- **`apiVersion`**: This field specifies the API version of Kubernetes used.
- **`kind`**: This indicates the type of resource for creating. For services it is `Service`
- **`metadata`**: This section contains metadata about the `Service`, such as its name, namespace, labels, and annotations
- **`spec`**: This section contains the specification of the `Service`, including the service type, selector for pods
- **`spec.ports`**:  This section defines the ports mapping configuration for the Service
## 2
>[!Task Description]
> Write a `Service` spec for your pod(s) and deploy the `Service` .

- First I change my `Pod` spec and created another one alongside:
	![[Pasted image 20250218231916.png]]
- Then I created a `Service` using the following spec:
	```yaml
	apiVersion: v1  
	kind: Service  
	metadata:  
	  name: current-time-server-service  
	spec:  
	  type: ClusterIP  
	  selector:  
	    app: current-time-server  
	  ports:  
	    - protocol: TCP  
	      port: 80  
	      targetPort: 8080
	```
	![[Pasted image 20250218233734.png]]
- Finally, I deployed them all:
	![[Pasted image 20250218232152.png]]
## 3
>[!Task Description]
> With `kubectl` , get the `Services` and `describe` them.

- `get`
	![[Pasted image 20250218233826.png]]
- `describe`
	![[Pasted image 20250218233834.png]]
## 4
>[!Task Description]
> Make sure that pods can communicate between each other using DNS names, check pods addresses.

- We could see that the service has been actually deployed and explored my pods' endpoints
	![[Pasted image 20250218233949.png]]
### IP
- Here are the pods IP addresses inside the cluster
	![[Pasted image 20250218232945.png]]
- We can make intercommunication without a service by these IP
	![[Pasted image 20250218234349.png]]
	![[Pasted image 20250218234355.png]]
- However, now it is also possible to communicate **by the same IP through the service**: `10.105.84.72` and `80` port
	![[Pasted image 20250218234750.png]]
	![[Pasted image 20250218234815.png]]
### DNS
- Apart from that we can now refer to the service **by DNS name**
	![[Pasted image 20250219005852.png]]
	![[Pasted image 20250219005917.png]]
-  This is possible because of the DNS server `kybe-system/coredns` and `kybe-system/kube-dns` service
	![[Pasted image 20250219010058.png]]
- We see that our pods have predefined domains that are resolved by the `kube-dns` service
	![[Pasted image 20250219010219.png]]
## 5
>[!Task Description]
> Delete any `Pod`, recreate it and check addresses again. Make sure that traffic is routed to the new `Pod` correctly

- I deleted the pod `current-time-server-pod-2` and create it again
	```shell
	kubectl delete -f pod-2.yaml
	kubectl apply -f pod-2.yaml
	```
	![[Pasted image 20250219212720.png]]
- We can notice that this pod changed its IP address
	![[Pasted image 20250219213938.png]]
- Then I made an HTTP request to my service 10 times
	```shell
	for i in {1..10}; do kubectl exec current-time-server-pod-1 -- curl http://current-time-server-service/time; done
	```
	![[Pasted image 20250219213133.png]]
- We can see the this pods has corresponding logs
	```shell
	kubectl logs current-time-server-pod-2
	```
	![[Pasted image 20250219213621.png]]
## 6
>[!Task Description]
> Learn about `Loadbalancer` and `NodePort` .
### ClusterIP
- **Description**: This is the default Service type. Such a Service exposes the service on a cluster-internal IP. It makes the Service accessible only from within the cluster. 
- **Use Case**: Necessary for internal communication between Pods within the cluster. For example, microservices that need to communicate with each other can use a `ClusterIP` Service.  
### NodePort  
- **Description**: A `NodePort` service exposes the `Service` on each Node’s IP at a static port. A request to the Node’s IP at the NodePort is forwarded to the Service.  
- **Use Case**: Useful for development and testing, or for when you want to expose the Service externally without a load balancer. It’s a straightforward method to expose a service available via any of the cluster nodes.  
### LoadBalancer  
- **Description**: This type of Service provisions an external load balancer (if the cloud provider supports this feature), which directs traffic to the `ClusterIP` of the Service. The load balancer is assigned a public IP address.  
- **Use Case**: Suitable for production applications where you want to expose a Service to the internet or external clients. This is commonly used with cloud providers like AWS, GCP, or Azure.  
### ExternalName  
- **Description**: An `ExternalName` Service maps the Service to a DNS name. It does not proxy any traffic but serves as a way to reference an external service based on a name.  
- **Use Case**: Useful for integrating external services into the Kubernetes cluster. Instead of using an IP or a fixed address, you can reference the external service by its DNS name.
## 7
>[!Task Description]
> Deploy an external `Service` to access your application from outside, e.g., from your local host

- I decided to use `LoadBalancer` service type
- Here is the updated `service.yaml` file content
	```yaml
	apiVersion: v1  
	kind: Service  
	metadata:  
	  name: current-time-server-service  
	spec:  
	  type: LoadBalancer  
	  selector:  
	    app: current-time-server  
	  ports:  
	    - protocol: TCP  
	      port: 80  
	      targetPort: 8080  
	  externalIPs:  
	    - 192.168.49.2
	```
- Now let's recreate our service
	![[Pasted image 20250219221557.png]]
- First, let's try to connect to the minikube node that is running inside a Docker container
	```
	docker exec -it minikube curl http://localhost:30467/time -w '\n'
	```
	![[Pasted image 20250219221844.png]]
- After, let's I tunnel node's connection to my host machine
	```shell
	minikube service current-time-server-service
	```
	![[Pasted image 20250219222009.png]]
- Now we can access it locally
	![[Pasted image 20250219222104.png]]
- This is funny to understand that there are so many ports in such a chain:
	`Pod's 8080 --> Node's Internal 80 --> Node's External 30467 --> Host's 52413`
## 8
>[!Task Description]
> Put the results into report

✅

---
# Task 5 - k8s Deployment
> `Deployment` is Kubernetes manifest to manage Pods automatically by [Controller](https://kubernetes.io/docs/concepts/architecture/controller/). It helps to release, scale and upgrade Pods.

## 1
>[!Task Description]
> Figure out the necessary `Deployment` spec fields.
- **`apiVersion`**: This field specifies the API version of Kubernetes used.
- **`kind`**: This indicates the type of resource for creating. For deployments it is `Deployment`
- **`metadata`**: This section contains metadata about the Deployment, such as its name, namespace, labels, and annotations. The name of the Deployment is particularly important for managing and identifying it.
- **`spec`**: The specification for the Deployment, which includes the following necessary fields:
	- **`replicas`**: Specifies the desired number of Pod replicas. This is how many instances of your application you want to run.
	- **`selector`**: A label selector that identifies the Pods that belong to this Deployment. It must match the labels defined in the Pod template.
- **`template`**: A Pod template used to create the Pods. It contains the specification of the Pods, including metadata and spec for the containers.
## 2
>[!Task Description]
> Make sure that you wiped previous `Pod` manifests. Write a `Deployment` spec for your pod(s) and deploy the application.

- Firstly, I deleted pods
	![[Pasted image 20250219224845.png]]
- I created `deployment.yaml` file with 2 pod replicas remaining pods' configuration the same
	![[Pasted image 20250219225109.png]]
- Below is the content of this file
	```yaml
	apiVersion: apps/v1  
	kind: Deployment  
	metadata:  
	  name: current-time-server-deployment  
	spec:  
	  replicas: 2  
	  selector:  
	    matchLabels:  
	      app: current-time-server  
	  template:  
	    metadata:  
	      labels:  
	        app: current-time-server  
	    spec:  
	      containers:  
	        - name: current-time-server  
	          image: iskanred/current-time-server:1.0.0  
	          ports:  
	            - containerPort: 8080
	```
- Here is the result of deploying the deployment
	```shell
	kubectl apply -f deployment.yaml
	```
	![[Pasted image 20250219225434.png]]
## 3
>[!Task Description]
> With `kubectl`, get the `Deployments` and describe them.

- I called `get` and `describe` commands for the deployment
	![[Pasted image 20250219225621.png]]
- We here
	- `StrategyType` for upgrading the deployment with its configuration
	- `NewReplicaSet` for controlling pods' replicas creation and management
	- and other useful information
## 4
>[!Task Description]
> Update your `Deployment` manifest to scale your application to three replicas.

- The most convenient way to scale the number of replicas of a running deployment is to use `kubectl scale` command.
- It may be useful for temporary increasing the number of pods in conditions of high traffic load.
- Here is the scaling to 3 replicas:
	```shell
	kubectl scale deployment current-time-server-deployment --replicas=3
	```
	![[Pasted image 20250219230457.png]]
## 5
>[!Task Description]
> Access pod shell and logs using `Deployment` labels.

- We access all deployment's pods logs
	```shell
	kubectl logs -l app=current-time-server
	```
	![[Pasted image 20250219231230.png]]
- Besides, for accessing logs of the specific we should list all the pods of the deployment using the label
	```shell
	kubectl get pods -l app=current-time-server
	```
	![[Pasted image 20250219231830.png]]
- And then get logs of a desired pod
	```shell
	kubectl logs current-time-server-deployment-555c44f85d-8dhv8
	```
	![[Pasted image 20250219231917.png]]
- In the same way we can access pod's shell
	```shell
	kubectl exec -it current-time-server-deployment-555c44f85d-8dhv8 -- bash
	```
	![[Pasted image 20250219232554.png]]
## 6
>[!Task Description]
> Make any application configuration change in your `Deployment` yaml and try to update the application. Monitor what are happened with pods (`--watch`).

- For application update I added a `command` for pods' containers
	```yaml
	apiVersion: apps/v1  
	kind: Deployment  
	metadata:  
	  name: current-time-server-deployment  
	spec:  
	  replicas: 2  
	  selector:  
	    matchLabels:  
	      app: current-time-server  
	  template:  
	    metadata:  
	      labels:  
	        app: current-time-server  
	    spec:  
	      containers:  
	        - name: current-time-server  
	          image: iskanred/current-time-server:1.0.0  
	          command: ['sh', '-c', 'echo "Hello, Kubernetes!" && sleep 3600']  
	          ports:  
	            - containerPort: 8080
	```
- I applied the new deployment configuration and started watching for pods
	```shell
	kubectl apply -f deployment.yaml && \
	kubectl get pods -l app=current-time-server --watch
	```
	![[Pasted image 20250219235102.png]]
- We can check if the application was updated by accessing its logs
	![[Pasted image 20250219235428.png]]
## 7
>[!Task Description]
> Rollback to previous application version using `Deployment` .

- At first, let's check the history of the deployment
	```shell
	kubectl rollout history deployment/current-time-server-deployment 
	```
	![[Pasted image 20250219235542.png]]
- We can notice that the `REVISION` is so hight because I debugged the deployment upgrading process in the previous task
- Now let's rollout the deployment to the previous revision
	```shell
	kubectl rollout undo deployment/current-time-server-deployment && \
	kubectl get pods -l app=current-time-server --watch
	```
	![[Pasted image 20250220000247.png]]
- We see the status of our rollout is successful
- Let's check pods' logs
	![[Pasted image 20250220001034.png]]
- We see that the rollout was successful indeed!
- Finally, we can notice that our `ReplicaSet`s were updated by deployment controller
	![[Pasted image 20250220002359.png]]
## 8
>[!Task Description]
> Set up `requests` and `limits` for CPU and Memory for your application pods. Provide a PoC that it works properly. Explain and show what is happened when app reaches the CPU usage limit? And memory limit?

### **Exploring consumption**
- Below is the updated `deployment.yaml` file. Notice that I removed `container.command` from the manifest
	```yaml
	apiVersion: apps/v1
	kind: Deployment
	metadata:
	  name: current-time-server-deployment
	spec:
	  replicas: 2
	  selector:
	    matchLabels:
	      app: current-time-server
	  template:
	    metadata:
	      labels:
	        app: current-time-server
	    spec:
	      containers:
	        - name: current-time-server
	          image: iskanred/current-time-server:1.0.0
	          ports:
	            - containerPort: 8080
			  resources:  
			    requests:  
			      memory: 256Mi  
			      cpu: 0.5  
			    limits:  
			      memory: 512Mi  
			      cpu: 1
	```
- I applied this manifest
	![[Pasted image 20250220202849.png]]
- We can see that these limits/requests were applied to our pods
	![[Pasted image 20250220203752.png]]
- What is more we can notice that their QoS is indeed [`Burstable`](https://kubernetes.io/docs/concepts/workloads/pods/pod-qos/#burstable) because they do have memory and CPU limits/requests but they do not equal
	![[Pasted image 20250220204348.png]]
- Now let's check pods' metrics using minikube's  `metrics-server` addon
	```shell
	minikube addons enable metrics-server
	```
	![[Pasted image 20250220210415.png]]
- Now we can monitor our pods' performance Uusing `kubectl top` command
	```shell
	kubectl top pods -l app=current-time-server
	```
	![[Pasted image 20250220210456.png]]
- We see that my pods consume only `2m` which is 0.002 of CPU core cycle time and about `125Mi` of memory
### **Proof of concept**
- We can infer that current thresholds for limits for both memory and CPU are too big since the application does no use so much
- Let's determine some other boundaries and test if they work actually!
#### CPU limits
> My hypothesis is that **with smaller limit of CPU** time my **pods start slower**.
- This might happen because pods have lack of performance at the startup while application is configured: Spring beans should be created, JIT-compilation should "warm up" code regions, and etc.
##### Preparation
- To test startup duration I added `startupProbe` which access application's healthcheck endpoint
	```yaml
	apiVersion: apps/v1  
	kind: Deployment  
	<...>
	      containers:  
			startupProbe:  
			  httpGet:  
			    path: /actuator/health  
			    port: 8080  
			  periodSeconds: 1  
			  failureThreshold: 10000
	```
- We see that that the prober is configured to check if a pod's container is started `10000` times every `1` second
- To measure how long the pod is starting I implemented a simple `measure.sh`  script
	```shell
	#!/bin/zsh  
	  
	PODS=$(kubectl get po -l app=current-time-server)  
	  
	# Deleting pods if exist  
	if [[ -n $PODS ]]; then  
	  echo "There are some pods with the same labels running"  
	  kubectl delete -f deployment.yaml  
	  
	  while [[ $PODS != "" ]]  
	  do  
	    PODS=$(kubectl get po -l app=current-time-server)  
	    echo "Waiting for the pods to be deleted..."  
	    sleep 1  
	  done  
	  
	  echo "The pods are deleted"  
	fi  
	  
	kubectl apply -f deployment.yaml  
	echo "Waiting for pods to start..."  
	  
	seconds=0  
	while [[ $(kubectl get po -l app=current-time-server -o jsonpath='{.items[0].status.containerStatuses[0].started}') == "false" ]]  
	do  
	  (( seconds+=1 ))  
	  sleep 1;  
	done  
	  
	echo "Elapsed time: $seconds seconds"
	```
- It removes already existed pods with desired labels and starts a deployment. After the deployment is started the script increments the number of seconds until the status is not `false`. Finally, the script prints the number of elapsed time for pod to start.
- What is more, to make the process of testing faster I decreased our deployment to `1` replica only
	```yaml
	apiVersion: apps/v1  
	kind: Deployment  
	metadata:  
	  name: current-time-server-deployment  
	spec:  
	  replicas: 1
	<...>
	```
- In addition, I configured `requests` $=$ `limits` to simplify the process of testing `limits` making pods to have [`Guaranteed`](https://kubernetes.io/docs/concepts/workloads/pods/pod-qos/#guaranteed) QoS.
- Finally, I gave enough memory for my pods to be sure the memory is not a bottleneck when testing CPU.
- The final `deployment.yaml` is the following:
	![[Pasted image 20250221212125.png]]
##### Test
- Let's check if our CPU `limits` bounds actually work
- Firstly, let's start with `limits.cpu = 1`
	![[Pasted image 20250221212545.png]]
- Now, let's change the value to `limits.cpu = 0.5`
	![[Pasted image 20250221212507.png]]
- Let' continue with `limits.cpu = 0.25`
	![[Pasted image 20250221213052.png]]
- Finally, let' check the result with `limits.cpu = 0.175`
	![[Pasted image 20250221213244.png]]
- Okay, let's plot our results
	![[Pasted image 20250221214017.png]]
- We see that **`limits` mechanism actually works**!**. The smaller the `limits.cpu` the higher the amount of time for pod to start
#### Memory limits
> My hypothesis is that **with smaller limit of memory pods may be killed due to OOM**
- This might happen if an application has not enough memory to start / work.
##### Preparation
* To check if `limits.memory` actually works the strategy is similar to check the `limits.cpu` on a startup.
* However, here we have a **binary** evaluation: either the pod will start successfully or not.
- So here I can just set `limit.memory` value so small that pods will be killed almost immediately.
- To make startup faster I set `limits.cpu` $=$ `1`.
- What is more, I kept `requests` $=$ `limits`.
##### Test
- Let's check the value `limits.memory` $=$ `100Mi`
	![[Pasted image 20250222181222.png]]
- We see that our **`limit.memory` mechanism works too**! The container couldn't be started and tries to restart infinitely because `kubelet` kills it since it consumes more memory than the limit set.

## 9
>[!Task Description]
> Put the results into report

✅

--- 
# Task 6 - k8s Secrets
> `Secret` manifest is a quite similar to `configMap` . However, we use `Secret` to work with confidential application data. Kubernetes encode secrets in Base64 format.
## 1
>[!Task Description]
> Figure out the necessary Secret spec fields.
- **`apiVersion`**: This field specifies the API version of Kubernetes used.
- **`kind`**: This indicates the type of resource for creating. For secrets it is `Secret`.
- **`metadata`**: This section contains metadata about the `Secret`, such as its name, namespace, labels, and annotations.
- **`type`**: Defines the type of Secret. Common types include:
    - `Opaque`: The default type for arbitrary data.
    - `kubernetes.io/dockerconfigjson`: For Docker registry credentials.
    - `kubernetes.io/basic-auth`: For basic authentication credentials.
    - `kubernetes.io/ssh-auth`: For SSH authentication key.
- **`data`**: The data fields where each entry is base64-encoded.
- **`stringData`**: This is similar to `data`, but it allows you to specify the values in plaintext. Kubernetes will encode them in base64 automatically when you create the Secret. This can be more convenient for users.
- It's worth to notice that secrets do not have **`spec`** field in its manifest.
## 2
>[!Task Description]
> Create and apply a new `Secret` manifest. For example, it could be login and password to login to your app or something else

- I created a secret from the following `secret.yaml` manifest:
	```yaml
	apiVersion: v1  
	kind: Secret  
	metadata:  
	  name: current-time-server-secret  
	  labels:  
	    app: current-time-server  
	type: Opaque  
	stringData:  
	  secret: my-super-secret
	```
- And applied it
	![[Pasted image 20250222191146.png]]
## 3
>[!Task Description]
> With `kubectl` , get and describe your secret(s).

- `get`
	![[Pasted image 20250222191256.png]]
	![[Pasted image 20250222191519.png]]
- `describe`
	![[Pasted image 20250222191303.png]]
## 4
>[!Task Description]
> Decode your secret(s).

- I decoded the value of my secret using `base64`
	```
	kubectl get secret current-time-server-secret -o jsonpath='{.data.secret}' | base64 -d
	```
	![[Pasted image 20250222191710.png]]
## 5
>[!Task Description]
> Update your `Deployment` to reference to your secret as environment variable.

- I update the `deployment.yaml` as following:
	```yaml
	apiVersion: apps/v1  
	kind: Deployment  
	metadata:  
	  name: current-time-server-deployment  
	spec:  
	  replicas: 2  
	  selector:  
	    matchLabels:  
	      app: current-time-server  
	  template:  
	    metadata:  
	      labels:  
	        app: current-time-server  
	    spec:  
	      containers:  
	        - name: current-time-server  
	          image: iskanred/current-time-server:1.0.0  
	          ports:  
	            - containerPort: 8080  
	          resources:  
	            requests:  
	              memory: 512Mi  
	              cpu: 1  
	            limits:  
	              memory: 512Mi  
	              cpu: 1  
	          startupProbe:  
	            httpGet:  
	              path: /actuator/health  
	              port: 8080  
	            periodSeconds: 1  
	            failureThreshold: 10000  
	          env:  
	            - name: SECRET  
	              valueFrom:  
	                secretKeyRef:  
	                  name: current-time-server-secret  
	                  key: secret
	```
- Then I applied it
	![[Pasted image 20250222192732.png]]
## 6
>[!Task Description]
> Make sure that you are able to see your secret inside pod.

- To check if I can see my secret I tunneled the service to my local host using `minikube`
	![[Pasted image 20250222192936.png]]
- Finally, I could easily get the **MD5**-hashed value of my secret by accessing the `/secret` endpoint of my application
	![[Pasted image 20250222193127.png]]
- The response is
	```
	MD5 hash of secret is: ad44f1be904b9375641b666bd4bbc531
	```
- Let's compare this value with the hash value of my secret:
	![[Pasted image 20250222194931.png]]
- We see that they are actually equal
## 7
>[!Task Description]
> Put the results into report

✅

---
# Task 7 - k8s configMap
> `ConfigMap` is Kubernetes manifest to store application configuration setting in two ways: key-value pairs as environment variables and text (usually JSON) data as dedicated file into container filesystem. We usually use configMap to store non sensitive data as plain text

## 1
>[!Task Description]
> Figure out the necessary `configMap` spec fields.

- **`apiVersion`**: This field specifies the API version of Kubernetes used.
- **`kind`**: This indicates the type of resource for creating. For config maps it is `ConfigMap`.
- **`metadata`**: This section contains metadata about the `ConfigMap`, such as its name, namespace, labels, and annotations.
- **`data`**: A map of key-value pairs, where the keys are strings and the values can be strings or binary data encoded in base64. This field holds the configuration information.
- **`binaryData`**: (optional) Similar to `data`, but here values must be base64 encoded. It’s used for binary data (e.g., a configuration file stored in binary format).
## 2
>[!Task Description]
> Modify your `Deployment` manifest to set up some app configuration via environment variables

- I decided to use the same value for obtaining a secret but now from a config map, not a secret
- I reused the same `deployment.yaml` but edited the secret source to config map
	```yaml
	<...>
		env:  
		  - name: SECRET  
		    valueFrom:  
		      configMapKeyRef:  
		        name: current-time-server-config  
		        key: secret
	```
## 3
>[!Task Description]
> Create a new `configMap` manifest. In data spec, put some app configuration as key-value pair (it could be the same as in previous exercise). In the `Deployment.Pod` spec add the connection to key-value pair from `configMap` yaml file
- Below is my `configmap.yaml`
	```yaml
	apiVersion: v1  
	kind: ConfigMap  
	metadata:  
	  name: current-time-server-config  
	  labels:  
	    app: current-time-server  
	data:  
	  secret: my-config-secret
	```
- I applied it
	![[Pasted image 20250222201527.png]]
	![[Pasted image 20250222201534.png]]
	![[Pasted image 20250222201737.png]]
- Then, I applied my `deployment.yaml`
	![[Pasted image 20250222201612.png]]
	![[Pasted image 20250222201637.png]]
- Let's check again if secret exist inside my pods
	![[Pasted image 20250222201935.png]]
	![[Pasted image 20250222202023.png]]
- We see that it worked!
## 4
>[!Task Description]
> Create a new file like `config.json` file and put some json data into
- Here is `config.json`:
	```json
	{
	  "application": "current-time-server",
	  "object": {  
	    "name": "value"  
	  }  
	}	
	```
## 5
>[!Task Description]
> Create a new one `configMap` manifest. Connect `configMap` yaml file with `config.json` file to read the data from it.
- I created the following `current-time-server-config-json`:
	```shell
	kubectl create configmap current-time-server-config-json --from-file config.json
	```
	![[Pasted image 20250222204044.png]]
## 6
>[!Task Description]
> Update your `Deployment` to add `Volumes` and `VolumeMounts` .
- I updated my `deployment.yaml`
	```yaml
	<...>
	          volumeMounts:  
	            - name: current-time-server-config-json-volume  
	              mountPath: /current-time-server  
	      volumes:  
	        - name: current-time-server-config-json-volume  
	          configMap:  
	            name: current-time-server-config-json
	```
- And applied it
	![[Pasted image 20250222205811.png]]
## 7
>[!Task Description]
> With `kubectl`, check the `configMap` details. Make sure that you see the data as plain text
- We can check it see that it displays as a JSON text:
	```shell
	kubectl get configmap current-time-server-config-json -o jsonpath='{.data.config\.json}'
	```
	![[Pasted image 20250222210320.png]]
- However it also displays as a plain text when we do not specify config field
	![[Pasted image 20250222210449.png]]
## 8
>[!Task Description]
> Check the filesystem inside app container to show the loaded file data on the specified path.
- Let's check pods' filesystem
	![[Pasted image 20250222210627.png]]
- And we see that the volume `current-time-server-config-json-volume` were actually mounted to both my pods!
## 9
>[!Task Description]
> Put the results into report

✅

---
# Task 8 - k8s Namespace
> `Namespace` Kubernetes Manifest is designed for different projects and deployment environments isolation. With `Namespaces` we can separate App 1 deployment from App 2 deployment, manage (and isolate) cluster resources for them, define users list to have access either to App 1 or to App 2 deployment. Using `Namespaces` , we also can define a different environments like DEV, TEST, STAGE. In that way, `Namespaces` is a required feature for a real Kubernetes production clusters.

## 1
>[!Task Description]
> Figure out the necessary `Namespace` spec fields.
- **`apiVersion`**: This field specifies the API version of Kubernetes used.
- **`kind`**: This indicates the type of resource for creating. For namespace it is `Namespace`.
- **`metadata`**: This section contains metadata about the `ConfigMap`, such as its name, namespace, labels, and annotations.
	- **`name`**: The name of the Namespace. This must be a unique identifier **within the cluster** and typically follows DNS label formatting requirements (lowercase alphanumeric characters or '-' and must start and end with an alphanumeric character).
## 2
>[!Task Description]
> Create a two different `Namespaces` in your k8s cluster
- I created two manifests: `nginx-namespace.yaml` and `current-time-server.yaml`
- `nginx-namespace.yaml`
	```yaml
	apiVersion: v1  
	kind: Namespace  
	metadata:  
	  name: nginx-namespace  
	  labels:  
	    app: nginx
	```
- `current-time-server.yaml`
	```
	apiVersion: v1  
	kind: Namespace  
	metadata:  
	  name: current-time-server-namespace  
	  labels:  
	    app: current-time-server
	```
- And applied them
	![[Pasted image 20250222212031.png]]
## 3
>[!Task Description]
> Using `kubectl` , get and describe your `Namespaces`.
- `get`
	![[Pasted image 20250222212145.png]]
- `describe`
	![[Pasted image 20250222212212.png]]
## 4
>[!Task Description]
> Deploy two different applications in two different `Namespaces` with `kubectl`. By the way, it's acceptable even just to deploy the same objects (same previous app) in the different `Namespaces` but with different resources names.
- It may be already clear that I am going to deploy my `current-time-server` application with all its resources to `current-time-server-namespace` and a simple **nginx** application to `nginx-namespace`.
### `current-time-server`
- I created all the necessary resources
	![[Pasted image 20250222213156.png]]
- And it worked
	![[Pasted image 20250222213503.png]]

### `nginx`
- I created a simple Deployment with an **nginx** container. The manifest for it is `nginx-deployment.yaml`:
	```yaml
	apiVersion: apps/v1  
	kind: Deployment  
	metadata:  
	  name: nginx-deployment  
	  labels:  
	    app: nginx  
	spec:  
	  replicas: 1  
	  selector:  
	    matchLabels:  
	      app: nginx  
	  template:  
	    metadata:  
	      labels:  
	        app: nginx  
	    spec:  
	      containers:  
	        - name: nginx  
	          image: nginx:latest  
	          ports:  
	            - containerPort: 80
	```
- Also, I created a simple Service for make this Deployment accessible from outside. The manifest for it is `nginx-service.yaml`:
	```yaml
	apiVersion: v1  
	kind: Service  
	metadata:  
	  name: nginx-service  
	spec:  
	  type: LoadBalancer  
	  selector:  
	    app: nginx  
	  ports:  
	    - protocol: TCP  
	      port: 80  
	      targetPort: 80  
	  externalIPs:  
	    - 192.168.49.2
	```
- I applied these resources inside the `nginx-namespace` namespace
	![[Pasted image 20250222214210.png]]
- Finally, I could access the **nginx** application
	![[Pasted image 20250222214500.png]]
## 5
>[!Task Description]
> With `kubectl`, get and describe pods from different Namespaces witn `-n` flag.
- `get`
	![[Pasted image 20250222214601.png]]
- `describe`
	![[Pasted image 20250222214622.png]]![[Pasted image 20250222214640.png]]
## 6
>[!Task Description]
> Can you see and can you connect to the resources from different Namespaces?
- Yes. For example in `nginx-namespace`
	![[Pasted image 20250222214849.png]]
- Or in `current-time-server-namespace`
	![[Pasted image 20250222214944.png]]
## 7
>[!Task Description]
> Put the results into report

✅

# Post scriptum 
- You can find all the materials of this lab in my GitHub repo of the course
	https://github.com/iskanred/iu-devsecops-course/tree/main/lab-04
- Here is my file structure with files related to k8s for this lab
	![[Pasted image 20250222215210.png]]
---