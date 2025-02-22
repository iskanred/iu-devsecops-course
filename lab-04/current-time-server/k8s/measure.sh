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
