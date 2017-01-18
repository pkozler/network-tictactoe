#!/bin/bash

libreoffice --headless --convert-to pdf dokumentace.odt
python3 update.py

cd ./c_src/
make
cd ../java_src/
ant
cd ../c_bin/
sudo chmod u+x server
cd ../java_bin/
sudo chmod u+x Client.jar
cd ../

filename=${PWD##*/}
zip -r $filename c_src/ java_src/ c_bin/ java_bin/ dokumentace.pdf
