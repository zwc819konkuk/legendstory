#!/bin/bash

#运行主类
java_main_clazz=org.tinygame.legendstory.ServerMain

#////////////////////////

java_cmd="java -server -cp .:./lib/* -Xmx2048m ${java_main_clazz}"
nohup $java_cmd > /dev/null &