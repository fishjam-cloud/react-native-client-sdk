#!/bin/bash
set -e

echo "Running eslint for webdriverio-test javascript files \n"
eslint . --ext .ts,.tsx --fix --max-warnings 0

echo "Running prettier for webdriverio-test javascript files \n"
prettier --write . --ignore-path ./.eslintignore
