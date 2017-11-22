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

## Config
- Image currently setup for raspberry pi java (for cluster)
- TCP port 5555 used
