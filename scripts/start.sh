#!/bin/bash

# jar 파일이 존재하는 실제 경로
APP_FILE=/home/ubuntu/danum-backend/build/libs/danum-0.0.1-SNAPSHOT.jar

if pgrep -f "$APP_FILE" > /dev/null
then
  echo "App is already running"
  exit 0
fi

nohup java -jar $APP_FILE &

if [ $? -ne 0 ]; then
  echo "Failed to start app"
  exit 1
fi

echo "App started successfully"
