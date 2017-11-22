# Clojure RallyEvolve

## Build image
- To build new image:
  - Start docker Daemon
  - Build clojure standalone: ```lein uberjar```
  - Confirm that snapshot jar name matches that in Dockerfile
  - Build: ```docker build -t <imagename> .```
  - Start: ```docker run -i -t <imagename>:latest \bin\bash -e "INSTANCE=<nodeID>"```
- Note: to minimize image size/instance size check in stdout and use ```.dockerignore``` to remove unnecessary build inclusions:
  - ```Sending build context to Docker daemon  <size>MB```

## Usecase
- The image built using this repo is meant to run on a cluster node
- The node opens a server socket that accepts connections
- It executes the tests for the individual received by the connection, and returns the transformed individual
- It then accepts the next connection
- This is meant to be run in a docker swarm with multiple containers per host
- Testing is not parallel within a node

## Rally Language
- ```take_pacenote```  Get next pacenote
- ```steer_angle <n>```  Change steering angle
- ```dec_speed <n>```   Decrease speed
- ```inc_speed <n>```   Increase speed
- ```while_left_center``` While the direction of the car is left of the center of the road
- ```while_right_center``` While the direction of the car is left of the center of the road
- ```if_obs_path```
- ```if_caution```  If pacenote is caution
- ```if_right_hairpin```
- ```if_left_hairpin```
- ```if_sliding```

## Config
- Image currently setup for raspberry pi java (for cluster)
- TCP port 5555 used
