#!/bin/sh

curl -sLO https://raw.githubusercontent.com/babashka/babashka/master/install
chmod +x install
./install --dir .
./bb build
