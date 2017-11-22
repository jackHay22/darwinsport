#!/bin/bash
RED='\033[0;31m'
NC='\033[0m'
printf "Starting ${RED}RallyEvolve${NC} node ${1} \n"
exec java -jar /app.jar $1
