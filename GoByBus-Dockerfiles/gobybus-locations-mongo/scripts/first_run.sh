#!/bin/bash
USER=${MONGODB_USERNAME:-mongo}
PASS=${MONGODB_PASSWORD:-mongo1}
DB=${MONGODB_DBNAME:-admin}
ADMIN_PASS=${ADMIN_PASSWORD:-qwerty1}
if [ ! -z "$MONGODB_DBNAME" ]
then
    ROLE=${MONGODB_ROLE:-dbOwner}
else
    ROLE=${MONGODB_ROLE:-dbAdminAnyDatabase}
fi

# Start MongoDB service
/usr/bin/mongod --dbpath /data --nojournal &
while ! nc -vz localhost 27017; do sleep 1; done

# Create User
echo "Creating user: \"$USER\"..."

mongo $DB --eval "db.createUser({ user: '$USER', pwd: '$PASS', roles: [ { role: '$ROLE', db: '$DB' } ] });"
mongo $DB --eval "db.createUser({ user: 'admin', pwd: '$ADMIN_PASS', roles: [ { role: 'userAdminAnyDatabase', db: 'admin' } ] });"

# Create Indexes
mongo $DB --eval "db.locationData.createIndex( { firstLine: 1, brigade: 1, time: 1 }, { unique: true } );"
mongo $DB --eval "db.locationData.createIndex( { time: 1 } );"
mongo $DB --eval "db.locationData.createIndex( { firstLine: 1, brigade: 1, time: 1 } );"
mongo $DB --eval "db.locationData.createIndex( { firstLine: 1, time: 1 } );"

#descending (-1)

# Stop MongoDB service
/usr/bin/mongod --dbpath /data --shutdown

echo "========================================================================"
echo "MongoDB User: \"$USER\""
echo "MongoDB Password: \"$PASS\""
echo "MongoDB Database: \"$DB\""
echo "MongoDB Role: \"$ROLE\""
echo "========================================================================"
echo " "
echo "========================================================================"
echo "MongoDB Admin: \"admin\""
echo "MongoDB Admin pass: \"$ADMIN_PASS\""
echo "MongoDB Database: \"admin\""
echo "MongoDB Role: \"userAdminAnyDatabase\""
echo "========================================================================"

rm -f /.firstrun
