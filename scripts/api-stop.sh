#! /bin/sh

# declare variable
PID_FILE="api.pid"

# declare function
function check_if_pid_file_exists {
  if [ ! -f $PID_FILE ]
  then
    echo "PID file not found: $PID_FILE"
    exit 1
  fi
}

function check_if_process_is_running {
if ps -p $(print_process) > /dev/null 2>&1
then
  true; return
else
  false; return
fi
}

function print_process {
  echo $(<"$PID_FILE")
}

### stop procedure start
check_if_pid_file_exists
if ! check_if_process_is_running
then
  echo "Process $(print_process) already stopped"
  exit 0
fi

kill -TERM $(print_process)
echo -ne "Waiting for process to stop"
NOT_KILLED=1

for i in {1..20}; do
  if check_if_process_is_running
  then
    echo -ne "."
    sleep 1
  else
    NOT_KILLED=0
  fi
done

echo

if [ $NOT_KILLED = 1 ]
then
  echo "Cannot kill -TERM process $(print_process)"
  echo "So.. start to kill -KILL process $(print_process)"
  kill -KILL $(print_process)
  sleep 2
fi

echo "Process $(print_process) stopped"