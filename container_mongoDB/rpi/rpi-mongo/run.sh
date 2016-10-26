#!/bin/bash
set -e
if [ -f /nodes ]; then
        cat /nodes >> /etc/hosts
fi
if [ "${1:0:1}" = '-' ]; then
        set -- mongod "$@"
fi
exec "$@"