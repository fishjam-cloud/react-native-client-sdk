#!/bin/bash

# Terminate on errors
set -e

printf "Synchronising submodules... "
git submodule sync --recursive >> /dev/null
git submodule update --recursive --remote --init >> /dev/null
printf "DONE\n\n"

file="./protos/fishjam/peer_notifications.proto"

printf "Compiling: file $file for react-native\n"
protoc --plugin=./node_modules/.bin/protoc-gen-ts_proto --ts_proto_out=./packages/react-native-client/src/ $file
printf "DONE for react native\n\n"

printf "Compiling: file $file for android\n"
protoc -I=./protos --java_out=packages/android-client/FishjamClient/src/main/java/ --kotlin_out=packages/android-client/FishjamClient/src/main/java/ $file
printf "DONE for android\n\n"

