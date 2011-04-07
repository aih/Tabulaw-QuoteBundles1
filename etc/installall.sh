#!/bin/bash

pushd org.springextensions.db4o/1.0.0e
./installall.sh
popd
pushd gdata
./installall.sh
popd
pushd step2
./installall.sh
popd
pushd gwt
./installall.sh
popd
