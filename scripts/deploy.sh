LOCAL_DIR=/Users/user/workspace-queryholic/house-keeping-book
REMOTE_HOME=/home/centos

scp -i $LOCAL_DIR/toast-qh-key.pem $LOCAL_DIR/target/api.jar centos@133.186.223.185:$REMOTE_HOME/apps/api/
scp -i $LOCAL_DIR/toast-qh-key.pem $LOCAL_DIR/scripts/*.sh centos@133.186.223.185:$REMOTE_HOME/scripts/

ssh -i $LOCAL_DIR/toast-qh-key.pem centos@133.186.223.185 "chmod +x $REMOTE_HOME/apps/api/api.jar"
ssh -i $LOCAL_DIR/toast-qh-key.pem centos@133.186.223.185 "chmod +x $REMOTE_HOME/scripts/*.sh"