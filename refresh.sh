echo 'start...\n'

curdir=${PWD#*/}
echo $curdir

mvn clean eclipse:clean
mvn eclipse:eclipse #-DdownloadSources=true -Dwtpversion=2.0
mvn dependency:copy-dependencies
echo '\nend...'