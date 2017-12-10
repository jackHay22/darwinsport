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

## Graphical mode
- Darwin has its own builtin graphical abilities
- DarwinSport has a real-time top-down soccer game window.  This should typically be used outside of a run
  - Note: graphical mode slows computation to realtime (throttled by framerate)

## Usecase
- The image built using this repo is meant to run on a cluster node
- The node opens a server socket that accepts connections
- It executes the tests for the individual received by the connection, and returns the transformed individual
- It then accepts the next connection
- This is meant to be run in a docker swarm with multiple containers per host
- Testing is not parallel within a node

## Player Attributes
```
    { :location (55 55)                         ;current location of player
      :facing-angle 45                          ;angle the player is currently facing
      :assigned-image 0                         ;Index of image to use in graphic mode: 0-3 for team 1, 4-7 for team 2
      :team-locations (() ())                   ;locations of all other players on this player's team
      :opponent-locations (() ())               ;locations of all players on the opposing side
      :team 1 or 2                              ;location of the ball
      :possessing-ball? false                   ;indication of whether the player is possessing the ball
      :defined-decisions () }                   ;decision code (see below) that the player is capable of
```

## Decision Code      
TODO: reasoning that the player is capable of: this is what is developed genetically and sent to the compute node     

## Config
- Image currently setup for raspberry pi java (for cluster)
- TCP port 5555 used to communicate across local swarm network
- Graphical mode should not be enabled in swarm mode as it slows computation to real time and introduces inefficiencies.
