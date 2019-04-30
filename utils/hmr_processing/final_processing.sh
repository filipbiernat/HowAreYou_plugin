#!/bin/bash
#set -x
file="model.hmr.hmr"

sed -i.bak "s/'\(-.*\)'/\1/g" $file