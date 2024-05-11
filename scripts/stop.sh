#!/bin/bash

#애플리케이션 실행 포트
APP_PORT=8080

# 실행중인 프로세스 id 가져오기
PID=$(lsof -ti tcp:$APP_PORT)

if [ -z "$PID" ]; then
    echo "App is already stopped"
    exit 0
fi

# 프로세스 종료 시도
kill -TERM $PID

# 종료 상태 확인
if ps -p $PID > /dev/null
then
  echo "App stop failed"
  exit 1
else
  echo "App stopped successfully"
  exit 0
fi
