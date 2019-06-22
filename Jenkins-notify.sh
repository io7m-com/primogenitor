#!/bin/sh

fatal()
{
  echo "fatal: $1" 1>&2
  exit 1
}

if [ $# -ne 1 ]
then
  fatal "usage: status"
fi

STATUS="$1"
shift

if [ -z "${JENKINS_JMS_BROKER}" ]
then
  fatal "JENKINS_JMS_BROKER not set"
fi
if [ -z "${JENKINS_JMS_USER}" ]
then
  fatal "JENKINS_JMS_USER not set"
fi
if [ -z "${JENKINS_JMS_PASSWORD}" ]
then
  fatal "JENKINS_JMS_PASSWORD not set"
fi
if [ -z "${JENKINS_JMS_QUEUE}" ]
then
  fatal "JENKINS_JMS_QUEUE not set"
fi

mvn \
  -U \
  org.apache.maven.plugins:maven-dependency-plugin:3.1.1:get \
  -DgroupId=com.io7m.jsay \
  -DartifactId=com.io7m.jsay \
  -Dversion=0.0.1 \
  -Dclassifier=main \
  -Dtransitive=false

cp .repository/com/io7m/jsay/com.io7m.jsay/0.0.1/com.io7m.jsay-0.0.1-main.jar jsay.jar

case ${STATUS} in
  success)
cat >build.txt <<EOF
Build ${JOB_NAME} ${BUILD_DISPLAY_NAME} succeeded
EOF
    ;;
  *)
cat >build.txt <<EOF
Build ${JOB_NAME} ${BUILD_DISPLAY_NAME} failed
EOF
    ;;
esac

cat >args.txt <<EOF
--broker-uri
${JENKINS_JMS_BROKER}
--user
${JENKINS_JMS_USER}
--password
${JENKINS_JMS_PASSWORD}
--address
${JENKINS_JMS_QUEUE}
--file
build.txt
EOF

exec java -jar jsay.jar @args.txt
