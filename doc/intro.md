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
      :assigned-image "file"                    ;file of image to use in graphic mode
      :team ()                                  ;all other players on this player's team
      :opponent ()                              ;all players on the opposing side
      :ball-location (() ())                    ;location of the ball
      :possessing-ball? false                   ;indication of whether the player is possessing the ball
      :open? true                               :whether or not player is describing himself as open
      :defined-decisions () }                   ;decision code (see below) that the player is capable of
```

## Decision Code      
Syntax example:
```
and self-ball-posessed? self-space? : self-dribble give-directive
and self-ball-posessed? !self-space? team-mate-open? : action-short-pass-forward
or self-in-defensive-third? !self-space? : action-clear
```
- All predicates can be inverted by adding ``` ! ``` on the front.
- All predicate decision pairs are structured ``` and|or p p ... p : a a ... a ```
- The following are valid predicates:
  - ``` self-ball-posessed? ```
  - ``` self-space? ```
  - ``` team-mate-open? ```
  - ``` self-defensive-third? ```
  - ``` self-offensive-third? ```
  - ``` team-possessing-ball? ```
  - ``` opponent-possessing-ball? ```
    - (And inverses)
- The following are valid actions:
  - ``` action-longest-pass-forward ```
  - ``` action-self-dribble-forward ```
  - ``` directive-shoot ```
  - ``` directive-self-pass ```
  - ``` directive- ```
  - ``` action-short-pass-forward ```
  - ``` action-clear ```
  - ``` action-shoot ```
  - ``` action-tackle ```
  - ``` action-follow-ball ```

## Config
- Image currently setup for raspberry pi java (for cluster)
- TCP port 5555 used to communicate across local swarm network
- Graphical mode should not be enabled in swarm mode as it slows computation to real time and introduces inefficiencies.
