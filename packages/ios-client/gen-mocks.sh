#!/bin/bash
set -eu
cd "$(dirname "$0")"
.build/checkouts/mockingbird/mockingbird generate --project project.json \
  --output-dir Tests/FishjamClientTests/FishjamClientMocks \
  --testbundle FishjamClientTests \
  --targets FishjamClient
