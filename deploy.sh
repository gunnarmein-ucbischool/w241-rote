echo "pw is a AND"
curl -X POST -u "gmein@eastsideprep.org" --data-binary @"target/Rote-1.0-SNAPSHOT.war" https://rote.scm.azurewebsites.net/api/wardeploy
