#!/bin/bash
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
PLAYER='\xE2\x9A\xBD'
printf "${PLAYER}  Starting ${RED}DarwinSport${NC} node ${YELLOW}${1}${NC} \n"
exec java -jar /app.jar $1
