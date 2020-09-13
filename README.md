PdfJumbler
==========

[![Build Status](https://travis-ci.org/mgropp/pdfjumbler.svg?branch=master)](https://travis-ci.org/mgropp/pdfjumbler)

Installation
------------
PdfJumbler requires a Java Runtime Environment (JRE).
You can download one at <http://www.java.com> or <https://adoptopenjdk.net/>.

Windows users can simply download and run the installer (setup-pdfjumbler.exe),
which creates a start menu entry for PdfJumbler.

Alternatively, there is a runnable jar file (just double-click), pdfjumbler.jar.


User Interface
--------------
The user interface is actually rather simple:
pages can be moved around using drag&drop (or the
keyboard, see below), and for more complex operations
there's a second page list available to the left
(just pull it out).


Keyboard Shortcuts
------------------
* Ctrl+O       Open file
* Ctrl+S       Save file
* \+           Zoom in
* \-           Zoom out
* Alt+Up       Move page up
* Alt+Down     Move page down
* Del          Delete page
* Ctrl+Z       Undo
* Ctrl+Y       Redo
* Ctrl+R       Rotate clockwise
* Ctrl+Shift+R Rotate counter-clockwise


Command Line
------------
PdfJumbler accepts pdf files as command line arguments.

Several settings can be changed using Java system properties:

* `pdfjumbler.editor`: sets the editor plugin (if installed; previous plugins are no longer supported)
	 * PDFBox: `net.sourceforge.pdfjumbler.pdfbox.PdfEditor`

* `pdfjumbler.renderer`: sets the renderer plugin (if installed; previous plugins are no longer supported)
	 * PDFBox: `net.sourceforge.pdfjumbler.pdfbox.PdfRenderer`

* `pdfjumbler.lookandfeel`: sets the user interface look-and-feel
	Possible values depend on the installed Swing look-and-feels.
	Run PdfJumbler on the command line and set the property to ?
	to see a list.

* `user.language`: sets the program language	
	So far, `de` (German), `es` (Spanish), `ru` (Russian) and `en` (English; default)
	localizations are available.

### Example ###
```
java -Dpdfjumbler.editor=net.sourceforge.pdfjumbler.pdfbox.PdfEditor -jar pdfjumbler.jar foo.pdf bar.pdf
```
