#!/bin/bash
brew install swift-format 
yarn
chmod +x .githooks/*
cp .githooks/* .git/hooks
cp FishjamClientDemo/Release.xcconfig FishjamClientDemo/Debug.xcconfig