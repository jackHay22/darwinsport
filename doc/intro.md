# Clojure DarwinSport

## Build image
- To build new image:
  - Start docker Daemon
  - Build clojure standalone: ```lein uberjar```
  - Confirm that snapshot jar name matches that in Dockerfile
  - Build: ```docker build -t <imagename> .```
  - Start: ```docker run -i -t <imagename>:latest \bin\bash -e "INSTANCE=<nodeID>"```
- Note: to minimize image size/instance size check in stdout and use ```.dockerignore``` to remove unnecessary build inclusions:
  - ```Sending build context to Docker daemon  <size>MB```

## Resources
- Player config files can be found in ``` testfiles/players ```
- Decision code files can be found in ``` testfiles/decisionfiles ```
- Graphical resources can be found in ``` resources/images ```

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
  {:location (400 350)
   :facing-angle 0
   :possessing-ball? false
   :open? true
   :user-controlled? true                         ;signifies if a player can be user controlled   
   :autonomous-when-idle? true                    ;if player can be user controlled, this will access defined decisions in the absence of user input
   :assigned-image "images/players/team1player4.png"
   :defined-decisions "testfiles/decisionfiles/offensive_decisions.txt"}
```

## Decision Code      
Syntax example:
```
and ball-settle-radius? !self-ball-possessed? : action-settle-ball
and self-ball-possessed? self-space? shooting-range? : action-shoot
and self-ball-possessed? !self-space? team-mate-open? : action-leading-pass
and self-ball-possessed? self-space? : action-dribble-forward
and !self-ball-possessed? team-possessing-ball? : action-forward-run
and self-closest-to-ball? !opponent-possessing-ball? : action-recover-ball
and !opponent-possessing-ball? ball-forward? : action-forward-run
```
- IMPORTANT NOTE: later actions have the potential to overwrite actions at the beginning unless ``` :first-decision-only? true ``` is specified in config!
  - i.e. if an instruction causes the player to move in one direction, a future action in a different direction will overwrite that state
  - Therefore, actions should be designed to be clear cut and care should be exercised when writing actions that could potentially conflict
- All predicates can be inverted by adding ``` ! ``` on the front.
- All predicate decision pairs are structured ``` and|or p p ... p : a a ... a ```
- The following are valid predicates (not all listed here):
  - ``` self-ball-posessed? ```
  - ``` self-space? ```
  - ``` team-mate-open? ```
  - ``` self-defensive-third? ```
  - ``` self-offensive-third? ```
  - ``` team-possessing-ball? ```
  - ``` opponent-possessing-ball? ```
  - ``` tackle-range? ```
  - ``` close-to-goal? ```
    - (And inverses)
- The following are valid actions:
  - ``` action-longest-pass-forward ```
  - ``` action-self-dribble-forward ```
  - ``` action-short-pass-forward ```
  - ``` action-clear ```
  - ``` action-shoot ```
  - ``` action-tackle ```
  - ``` action-follow-ball ```
  - ``` action-defensive-drop ```

## Config
- Image currently setup for raspberry pi java (for cluster)
- TCP port 5555 used to communicate across local swarm network
- Graphical mode should not be enabled in swarm mode as it slows computation to real time and introduces inefficiencies.

## App build
``` javapackager -deploy -native image -outdir out -outfile darwinsport.app -srcfiles target/uberjar/darwinsport-0.1.0-SNAPSHOT-standalone.jar -appclass darwinsport.core -name "DarwinSport" -title "DarwinSport" -Bruntime= -Bicon=resources/images/Darwin.icns ```
