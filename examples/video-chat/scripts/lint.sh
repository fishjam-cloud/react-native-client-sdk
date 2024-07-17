#!/bin/bash
set -e

echo "Running eslint for video-chat javascript files \n"
eslint . --ext .ts,.tsx --fix --max-warnings 0

echo "Running prettier for video-chat javascript files \n"
prettier --write . --ignore-path ./.eslintignore
