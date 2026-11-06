#!/bin/bash
set -e
set -u

dest_dir="/opt/pdfjumbler"
desktop_file="/usr/local/share/applications/pdfjumbler.desktop"
jar_file="$dest_dir/pdfjumbler.jar"
launcher_file="/usr/local/bin/pdfjumbler"
icon_file="/usr/local/share/icons/hicolor/scalable/apps/pdfjumbler.svg"

sudo sh -s << EOF
set -e
rm -f "$desktop_file" "$launcher_file" "$icon_file" "$jar_file"

if [ -d "$dest_dir" ]
then
	rmdir "$dest_dir" 2>/dev/null || echo "Kept $dest_dir because it still contains other files, such as plugins."
fi

if command -v update-desktop-database >/dev/null 2>&1
then
	update-desktop-database -q "$( dirname "$desktop_file" )" || true
fi
EOF

echo "PdfJumbler has been uninstalled."
