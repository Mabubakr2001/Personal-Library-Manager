#!/bin/bash
set -e

mysqladmin ping -h localhost -uroot -p"$MYSQL_ROOT_PASSWORD" > /dev/null 2>&1