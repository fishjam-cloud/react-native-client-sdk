#!/bin/bash

# Terminate on errors
set -e

printf "Synchronising submodules... "
git submodule sync --recursive >> /dev/null
git submodule update --recursive --remote --init >> /dev/null
printf "DONE\n\n"

file="./protos/fishjam/peer_notifications.proto"

printf "Compiling: file $file\n"
protoc -I=./protos --java_out=FishjamClient/src/main/java/ --kotlin_out=FishjamClient/src/main/java/ $file
printf "DONE\n"
