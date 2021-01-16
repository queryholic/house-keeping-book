# start api
echo 'start api'

PROFILE="local"
HOME=/home/centos

nohup java -jar $HOME/apps/api/api.jar --spring.profiles.active=$PROFILE > /dev/null 2>&1 &