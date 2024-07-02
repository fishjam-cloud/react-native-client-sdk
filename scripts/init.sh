#!/bin/bash
brew install swift-format
brew install ktlint release-it
yarn
yarn build
chmod +x .githooks/*
cp .githooks/* .git/hooks
