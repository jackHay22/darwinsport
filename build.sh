#!/bin/bash
#requires lein to build in target
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
PLAYER='\xE2\x9A\xBD'
printf "${PLAYER}  Building ${RED}DarwinSport${NC} jar binary... ${YELLOW}${1}${NC} \n"
lein uberjar
printf "${PLAYER}  Building ${RED}DarwinSport${NC} app package... ${YELLOW}${1}${NC} \n"
javapackager -deploy \
    -native image \
    -outdir out \
    -outfile darwinsport.app \
    -srcfiles target/uberjar/darwinsport-0.1.0-SNAPSHOT-standalone.jar \
    -appclass darwinsport.core \
    -name "DarwinSport" \
    -title "DarwinSport" \
    -Bruntime= \
    -Bicon=resources/images/Darwin.icns
