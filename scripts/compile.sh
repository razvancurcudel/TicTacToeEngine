#exec > /dev/null 2>&1
version=v$1
rm -r $version/classes
mkdir -p $version/classes
javac -d javac -d $version/classes/ $version/bot/*.java
