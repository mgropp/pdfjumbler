#!/bin/bash
# By Alex Zaslavskis and Martin Gropp
set -e
set -u
set -o pipefail

required_java_version=21
dest_dir="/opt/pdfjumbler"
desktop_file="/usr/share/applications/pdfjumbler.desktop"
jar_file="$dest_dir/pdfjumbler.jar"
jar_file_url="https://github.com/mgropp/pdfjumbler/releases/latest/download/pdfjumbler.jar"
launcher_file="/usr/local/bin/pdfjumbler"

# Make sure Java is installed
if ! command -v java >/dev/null 2>&1
then
	echo "Java not found. Please install Java $required_java_version or newer!"
	exit 1
fi

java_version=$( java -version 2>&1 | sed -n 's/.* version "\([0-9]*\).*/\1/p' | head -n 1 )
if [ -z "$java_version" ] || [ "$java_version" -lt "$required_java_version" ]
then
	echo "PdfJumbler requires Java $required_java_version or newer, but found:"
	java -version
	exit 1
fi
echo "Java $java_version found."

# Create temp directory
tmpdir=$( mktemp -d )
trap "rm -rf \"$tmpdir\"" EXIT
cd "$tmpdir"

# Create launcher
cat > pdfjumbler << EOF
#!/bin/sh
exec java -jar "$jar_file" "\$@"
EOF

# Create desktop file
cat > pdfjumbler.desktop << EOF
[Desktop Entry]
Name=PdfJumbler
Exec=$launcher_file
Icon=pdf
Type=Application
Categories=GTK;GNOME;Utility;
Terminal=false
EOF

# Create script to move files and set permissions
cat > install.sh << EOF
#!/bin/sh
set -e
mv pdfjumbler.desktop "$desktop_file"
chown root:root "$desktop_file"
chmod 644 "$desktop_file"

mkdir -p "$dest_dir"
mv pdfjumbler.jar "$jar_file"
chown -R root:root "$dest_dir"
chmod 755 "$dest_dir"
chmod 644 "$jar_file"

mv pdfjumbler "$launcher_file"
chown root:root "$launcher_file"
chmod 755 "$launcher_file"
EOF

# Download
echo "Downloading PdfJumbler"
curl -fL "$jar_file_url" -o pdfjumbler.jar

# Install
sudo sh install.sh

echo
echo "Done. Enjoy!"
