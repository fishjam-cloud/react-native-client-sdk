#!/bin/bash

# Set the current directory as the working directory
DIRECTORY=$(pwd)

# Find and print all .swift files relative to the current directory
echo "Listing .swift files in the current directory: $DIRECTORY"
find "$DIRECTORY" -type f -name "*.swift" -print | sed "s|^$DIRECTORY/||"
