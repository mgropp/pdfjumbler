# Requires ImageMagick
pdfjumbler.ico: pdfjumbler-tiny.svg pdfjumbler.svg
	dir=`mktemp -d` && \
	convert -resize 16x16 -extent 16x16 -gravity center -antialias pdfjumbler-tiny.svg $${dir}/pdfjumbler-16.png && \
	convert -resize 24x24 -extent 24x24 -gravity center -antialias pdfjumbler-tiny.svg $${dir}/pdfjumbler-24.png && \
	convert -resize 32x32 -extent 32x32 -gravity center -antialias pdfjumbler.svg $${dir}/pdfjumbler-32.png      && \
	convert -resize 48x48 -extent 48x48 -gravity center -antialias pdfjumbler.svg $${dir}/pdfjumbler-48.png      && \
	convert -resize 64x64 -extent 64x64 -gravity center -antialias pdfjumbler.svg $${dir}/pdfjumbler-64.png      && \
	convert $${dir}/*.png ./pdfjumbler.ico && \
	rm -rf $${dir}
