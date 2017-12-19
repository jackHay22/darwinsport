# DarwinSport
Distributed platform for harnessing genetic programming to evolve simulated 2D soccer players.

## Use
- This program has two primary uses:
  - (Realtime) Graphical display of human developed or gp developed soccer teams
  - Distributed testing cluster node for genetically evolving players
  - Use:
    - Run ``` lein run <mode> ``` where mode is instance number or ``` -demo ```
    - Standalone jar build: ``` lein uberjar ```
    - jar run ```java -jar darwinsport-0.1.0-SNAPSHOT-standalone.jar <mode> ```

## Notes
- This program is meant as a soccer development testing node for Darwin, the work of Jack Hay and Nate Symer.
- Requires Java
- This repo is used for creating compute node images that are compatible with darwin running in a distributed setup
  - Run on Docker swarm with TCP load balancing on port 5555 in compute mode
- [Read the docs](doc/intro.md)

DarwinSport Copyright © 2017 Jack Hay,     Darwin Copyright © 2017 Jack Hay, Nate Symer
