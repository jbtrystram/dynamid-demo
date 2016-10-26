#!/bin/bash
set -e
if [ -f /nodes ]; then
        cat /nodes >> /etc/hosts
fi

MONGODB1=$(ping -c 1 node01 | head -1  | cut -d "(" -f 2 | cut -d ")" -f 1)
MONGODB2=$(ping -c 1 node02 | head -1  | cut -d "(" -f 2 | cut -d ")" -f 1)
MONGODB3=$(ping -c 1 node03 | head -1  | cut -d "(" -f 2 | cut -d ")" -f 1)
MONGODB4=$(ping -c 1 node04 | head -1  | cut -d "(" -f 2 | cut -d ")" -f 1)

echo "Waiting for startup.."
until curl --silent http://${MONGODB1}:28017/ 2>&1 | grep -q 'waiting for connections on port'; do
  printf '.'
  sleep 1
done
echo ''

echo curl --silent http://${MONGODB1}:28017/serverStatus\?text\=1 2>&1 | grep uptime | head -1
echo "MongoDB instances are ready.."

sleep 10

echo "Initiating replication."
echo SETUP.sh time now: $(date +"%T")
mongo --host ${MONGODB1}:27017 <<EOF
   var cfg = {
        "_id": "rs",
        "version": 1,
        "members": [
            {
                "_id": 0,
                "host": "${MONGODB1}:27017",
                "priority": 2
            },
            {
                "_id": 1,
                "host": "${MONGODB2}:27017",
                "priority": 1
            },
            {
                "_id": 2,
                "host": "${MONGODB3}:27017",
                "priority": 1
            },
            {
                "_id": 3,
                "host": "${MONGODB4}:27017",
                "priority": 1
            }
        ]
    };
    rs.slaveOk();
    rs.initiate(cfg, { force: true });
    rs.reconfig(cfg, { force: true });

    db.getMongo().setReadPref('nearest');
EOF
