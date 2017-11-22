#!/bin/bash
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'
CAR='\xF0\x9F\x8F\x8E'
printf "${CAR}  Starting ${RED}RallyEvolve${NC} node ${YELLOW}${1}${NC} \n"
exec java -jar /app.jar $1
