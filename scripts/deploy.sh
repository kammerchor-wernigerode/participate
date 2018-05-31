#!/bin/bash

dieWith() {
    echo -e " --- $1" >&2
    clear
    exit 1
}

prop() {
    grep "$1" "$SCRIPT_LOCATION/deploy.properties" | cut -d'=' -f2
}

ckfile() {
    if [ -s "$1" ]; then
        return 0
    fi

    dieWith $2
}

clear() {
    cp "$PROJECT_ROOT/src/main/resources/application.properties.bak" "$PROJECT_ROOT/src/main/resources/application.properties"
    cp "$PROJECT_ROOT/src/main/resources/liquibase.properties.bak" "$PROJECT_ROOT/src/main/resources/liquibase.properties"
    rm "$PROJECT_ROOT/src/main/resources/liquibase.properties.bak"
    rm "$PROJECT_ROOT/src/main/resources/application.properties.bak"
}

usage() {
    cat <<EOM
Usage: $(basename "$0") [OPTIONS]...

  -u VALUE    Overrides the default remote user
  -h VALUE    Overrides the default remote host
  -t VALUE    Overrides the default temp directory
  -w VALUE    Overrides the default Tomcat webapp directory
EOM

    exit 2
}

SCRIPT_LOCATION=$(dirname `readlink -f "$0"`)
PROJECT_ROOT="$SCRIPT_LOCATION/.."

REMOTE_HOST="$(prop 'remote.host')"
REMOTE_USER="$(prop 'remote.user')"
TEMP_DIR="$(prop 'remote.tmp')"
TOMCAT_WEBAPP="$(prop 'tomcat.webapp')"

while getopts "u:h:t:w:m" options; do
    case $options in
        u)
			REMOTE_USER=$OPTARG
			;;
		h)
			REMOTE_HOST=$OPTARG
			;;
		t)
			TEMP_DIR=$OPTARG
			;;
		w)
			TOMCAT_WEBAPP=$OPTARG
			;;
		*)
			usage
			;;
	esac
done

echo " *** Performing file checks"
ckfile "$SCRIPT_LOCATION/deploy.properties" "\e[1mdeploy.properties\e[0m is empty or doesn't exist. Abort"
ckfile "$PROJECT_ROOT/src/main/resources/application.properties" "\e[1mapplication.properties\e[0m is empty or doesn't exist. Abort"
ckfile "$PROJECT_ROOT/src/main/resources/application.prod.properties" "\e[1mapplication.prod.properties\e[0m is empty or doesn't exist. Abort"
ckfile "$PROJECT_ROOT/src/main/resources/liquibase.properties" "\e[1mliquibase.properties\e[0m is empty or doesn't exist. Abort"
ckfile "$PROJECT_ROOT/src/main/resources/liquibase.prod.properties" "\e[1mliquibase.prod.properties\e[0m is empty or doesn't exist. Abort"
echo " *** Looks good. Go..."

cp "$PROJECT_ROOT/src/main/resources/application.properties" "$PROJECT_ROOT/src/main/resources/application.properties.bak"
cp "$PROJECT_ROOT/src/main/resources/application.prod.properties" "$PROJECT_ROOT/src/main/resources/application.properties"
cp "$PROJECT_ROOT/src/main/resources/liquibase.properties" "$PROJECT_ROOT/src/main/resources/liquibase.properties.bak"
cp "$PROJECT_ROOT/src/main/resources/liquibase.prod.properties" "$PROJECT_ROOT/src/main/resources/liquibase.properties"

if [ -z $(command -v mvn) ]; then
    dieWith "Maven is not installed"
fi

echo " *** Install Project"
echo
mvn -f "$PROJECT_ROOT" -U -Dmaven.test.skip=true -Pproduction clean resources:resources install || dieWith "Failed to execute Maven task"
echo
echo

ORIGINAL="$(find $PROJECT_ROOT/target/*.war.original -type f)" || dieWith "Could not find WAR file"
FILE_NAME=$(basename "$ORIGINAL")

echo " *** Copy $FILE_NAME to $REMOTE_HOST"
scp "$ORIGINAL" $REMOTE_USER@$REMOTE_HOST:$TEMP_DIR/$FILE_NAME >/dev/null 2>&1 || dieWith "Failed to copy war to $REMOTE_HOST"
echo " *** Shut down Apache Tomcat Service"
ssh $REMOTE_USER@$REMOTE_HOST "sudo systemctl stop tomcat.service" >/dev/null 2>&1 || dieWith "Failed to shut down Apache Tomcat Service"
echo " *** Deploy new Version"
ssh -t $REMOTE_USER@$REMOTE_HOST "cp $TEMP_DIR/$FILE_NAME $TOMCAT_WEBAPP/ROOT.war" >/dev/null 2>&1 || dieWith "Failed to copy new Version"
echo " *** Start Apache Tomcat Service"
ssh $REMOTE_USER@$REMOTE_HOST "sudo systemctl start tomcat.service" >/dev/null 2>&1 || dieWith "Failed to start Apache Tomcat Service"
sleep 3
echo " *** Deployment successful"
echo " *** Run Liquibase Update"
ssh $REMOTE_USER@$REMOTE_HOST "cd $TOMCAT_WEBAPP/ROOT/WEB-INF/classes && /usr/local/bin/liquibase update" >/dev/null 2>&1 || dieWith "Liquibase Update was not successful"
echo " *** Liquibase Update successful"
clear
echo " +++ Done"

exit 0
