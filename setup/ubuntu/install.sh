#!/bin/bash
# By Alex Zaslavskis and Martin Gropp
set -e
set -u
set -o pipefail

required_java_version=21
dest_dir="/opt/pdfjumbler"
desktop_file="/usr/local/share/applications/pdfjumbler.desktop"
jar_file="$dest_dir/pdfjumbler.jar"
launcher_file="/usr/local/bin/pdfjumbler"
icon_file="/usr/local/share/icons/hicolor/scalable/apps/pdfjumbler.svg"

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

# Find the latest release
latest_release_url=$( curl -fsSLI -o /dev/null -w '%{url_effective}' https://github.com/mgropp/pdfjumbler/releases/latest )
release_tag="${latest_release_url##*/}"
if [ -z "$release_tag" ] || [ "$release_tag" = "latest" ]
then
	echo "Could not determine the latest PdfJumbler release."
	exit 1
fi
echo "Latest release: $release_tag"
jar_file_url="https://github.com/mgropp/pdfjumbler/releases/download/$release_tag/pdfjumbler.jar"
icon_file_url="https://raw.githubusercontent.com/mgropp/pdfjumbler/$release_tag/src/main/resources/icons/pdfjumbler.svg"

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
Exec=$launcher_file %F
Icon=pdfjumbler
Type=Application
MimeType=application/pdf;
Categories=Office;
Terminal=false
EOF

# Create script to install files
cat > install.sh << EOF
#!/bin/sh
set -e
install -D -o root -g root -m 644 pdfjumbler.jar "$jar_file"
install -D -o root -g root -m 755 pdfjumbler "$launcher_file"
install -D -o root -g root -m 644 pdfjumbler.desktop "$desktop_file"
install -D -o root -g root -m 644 pdfjumbler.svg "$icon_file"

if command -v update-desktop-database >/dev/null 2>&1
then
	update-desktop-database -q "$( dirname "$desktop_file" )" || true
fi
EOF

# Download
echo "Downloading PdfJumbler"
curl -fL "$jar_file_url" -o pdfjumbler.jar
curl -fsSL "$icon_file_url" -o pdfjumbler.svg

# Install
sudo sh install.sh

echo
echo "Done. Enjoy!"
