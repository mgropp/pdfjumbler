#!/bin/bash
# By Alex Zaslavskis.

echo Java should be installed on your system...
if command -v java >/dev/null 2>&1 ; then
    echo "java found"
    echo "version: $(java -version)"
else
    echo "java not found. Please install java"
    exit 1
fi




echo Clone all files 
 curl   https://raw.githubusercontent.com/alex5250/PdfJumbler-Installer-Linux/main/config.config  -L  -o /tmp/pdfjumbler.desktop

sudo mv /tmp/pdfjumbler.desktop /usr/share/applications/pdfjumbler.desktop
sudo chmod  644  /usr/share/applications/pdfjumbler.desktop
mkdir -p /opt/pdfjumbler

DOWNLOAD_URL=$(curl  https://api.github.com/repos/mgropp/pdfjumbler/releases/latest \
        | grep browser_download_url \
        | grep .jar \
        | cut -d '"' -f 4)
sudo curl  -L  -o /tmp/pdfjumbler.jar   "$DOWNLOAD_URL"
 sudo mv /tmp/pdfjumbler.jar /opt/pdfjumbler/pdfjumbler.jar
sudo chmod  755  /opt/pdfjumbler/pdfjumbler.jar
echo "exec java -jar /opt/pdfjumbler/pdfjumbler.jar "$@" " > /tmp/pdfjumbler.sh

sudo mv /tmp/pdfjumbler.sh /usr/local/bin/pdfjumbler


sudo chmod 755 /usr/local/bin/pdfjumbler



echo Done ...Enjoy 
