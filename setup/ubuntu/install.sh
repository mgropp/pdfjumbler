#!/bin/bash
# By Alex Zaslavskis and Martin Gropp
set -e
set -u

dest_dir="/opt/pdfjumbler"
desktop_file="/usr/share/applications/pdfjumbler.desktop"
desktop_file_url="https://raw.githubusercontent.com/alex5250/PdfJumbler-Installer-Linux/main/config.config"
jar_file="$dest_dir/pdfjumbler.jar"
jar_file_url=$(
	curl  https://api.github.com/repos/mgropp/pdfjumbler/releases/latest \
	| grep browser_download_url \
	| grep .jar \
	| cut -d '"' -f 4 \
)
launcher_file="/usr/local/bin/pdfjumbler"

# Make sure Java is installed
if command -v java >/dev/null 2>&1
then
    echo "Java found."
    echo "Version:"
	java -version
else
    echo "Java not found. Please install Java!"
    exit 1
fi

# Create temp directory
tmpdir=$( mktemp -d )
trap "rm -rf \"$tmpdir\"" EXIT
cd "$tmpdir"

# Create launcher
cat > pdfjumbler << EOL
#!/bin/sh
exec java -jar "$jar_file" "\$@"
EOL

# Create desktop file
cat > pdfjumbler.desktop << EOL
[Desktop Entry]
Name=PdfJumbler
Exec=$launcher_file
Icon=pdf
Type=Application
Categories=GTK;GNOME;Utility;
Terminal=false
EOL

# Create script to move files and set permissions
cat > install.sh << EOF
#!/bin/sh
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
echo "Downloading files"
curl -L "$jar_file_url" -o pdfjumbler.jar

# Install
sudo sh install.sh

echo
echo "Done. Enjoy!"
