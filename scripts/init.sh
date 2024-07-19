#!/bin/bash
brew install swift-format ktlint release-it
npm install -g genversion typescript
yarn
yarn build
chmod +x .githooks/*
cp .githooks/* .git/hooks
