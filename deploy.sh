echo "pw is a AND"
curl -v -X POST -u "gmein:Powerlifter&1" --data-binary @"target/Rote-1.0-SNAPSHOT.war" https://rote.scm.azurewebsites.net/api/wardeploy
echo "waiting to deploy ..."
sleep 40
curl rote.azurewebsites.net

