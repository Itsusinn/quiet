#!/bin/bash
set -o errexit -o nounset -o pipefail
cd "`dirname $0`/.."

mvn compile exec:exec
