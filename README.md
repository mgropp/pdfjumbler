PdfJumbler
==========

[![Build Status](https://travis-ci.org/mgropp/pdfjumbler.svg?branch=master)](https://travis-ci.org/mgropp/pdfjumbler)

Installation
------------
PdfJumbler requires a Java Runtime Environment (JRE).
You can download one at <http://www.java.com>.
(Linux users should use their package management
system and install, for example, the packages
`default-jre` and `libopenjfx-java`,
MacOS usually comes with a preinstalled JRE).

Windows users can simply download and run the
installer (setup-pdfjumbler.exe), which
creates a start menu entry for PdfJumbler.

Alternatively, there is a runnable jar file (just
doubleclick), pdfjumbler.jar.


Plugins
-------
Both the installer and the runnable jar include
the iText editor and the JPedal renderer.

In the rare case that these standard plugins can't
handle a particular file, other plugins can be
installed simply by putting the respective jar files
in the installation directory (often something like
`C:\users\<user>\Local Settings\Application Data\
PdfJumbler`, or wherever you put the main jar file --
when in doubt search for files named `pdfjumbler-*.jar`).


User Interface
--------------
The user interface is actually rather simple:
pages can be moved around using drag&drop (or the
keyboard, see below), and for more complex operations
there's a second page list available to the left
(just pull it out).


Keyboard Shortcuts
------------------
* Ctrl+O    Open file
* Ctrl+S    Save file
* +         Zoom in
* -         Zoom out
* Alt+Up    Move page up
* Alt+Down  Move page down
* Del       Delete page
* Ctrl+Z    Undo
* Ctrl+Y    Redo


Command Line
------------
PdfJumbler accepts pdf files as command line arguments.

Several settings can be changed using Java system properties:

* `pdfjumbler.editor`: sets the editor plugin (if installed)	
	 * iText: `net.sourceforge.pdfjumbler.pdf.itext.PdfEditor`
	 * PDFBox: `net.sourceforge.pdfjumbler.pdf.PdfEditor`

* `pdfjumbler.renderer`: sets the renderer plugin (if installed)	
	 * JPedal: `net.sourceforge.pdfjumbler.pdf.jpedal.PdfRenderer`
	 * JPod: `net.sourceforge.pdfjumbler.pdf.jpod.PdfRenderer`

* `pdfjumbler.lookandfeel`: sets the user interface look-and-feel	
	Possible values depend on the installed Swing look-and-feels.
	Run PdfJumbler on the command line and set the property to ?
	to see a list.

* `user.language`: sets the program language	
	So far, `de` (German), `es` (Spanish), and `en` (English; default)
	localizations are available.

### Example ###
```
java -Dpdfjumbler.editor=net.sourceforge.pdfjumbler.pdf.PdfEditor -jar pdfjumbler.jar foo.pdf bar.pdf
```
