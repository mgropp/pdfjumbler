package net.sourceforge.pdfjumbler.i18n;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ListResourceBundle;

import javax.swing.KeyStroke;

/**
 * @author Martin Gropp
 */
public class PdfJumblerResources_de extends ListResourceBundle implements I18nKeys {
	@Override
	protected Object[][] getContents() {
		return new Object[][] {
			{ OPEN_DOCUMENT,            "Dokument öffnen" },
			{ SAVE_DOCUMENT,            "Dokument speichern" },
			{ FILE_FILTER_PDF,          "PDF-Dateien (*.pdf)" },
			{ OVERWRITE_FILE_TITLE,     "Überschreiben?" },
			{ OVERWRITE_FILE_TEXT,      "Die Datei %s existiert bereits. Überschreiben?" },
			{ ZOOM_OUT,                 "Verkleinern" },
			{ ZOOM_IN,                  "Vergrößern" },
			{ CLEAR_LIST,               "Alle löschen" },
			{ DELETE,                   "Löschen" },
			{ ABOUT,                    "Informationen" },
			{ LIST_DROP_PDFS_TO_EDIT,   "Ziehen Sie PDF-Dateien hierher\num sie zu bearbeiten!" },
			{ LIST_CLIPBOARD_EMPTY,     "Ablage\n(leer)" },
			{ CONFIRM_CLEAR_LIST_TITLE, "Löschen bestätigen" },
			{ CONFIRM_CLEAR_LIST_TEXT,  "Alles löschen?" },
			{ NO_PDF_EDITOR_TITLE,      "Fehler" },
			{ NO_PDF_EDITOR_TEXT,       "Es konnte kein PDF-Editor-Plugin geladen werden." },
			{ NO_PDF_RENDERER_TITLE,    "Fehler" },
			{ NO_PDF_RENDERER_TEXT,     "Es konnte kein PDF-Renderer-Plugin geladen werden." },
			{ PDF_PAGE_TITLE,           "%d (%s)" },
			{ MENU,                     "Menü" },
			{ MENU_EDITOR,              "PDF-Editor" },
			{ MENU_RENDERER,            "PDF-Anzeige" },
			{ PROGRESS_ABORT,           "Abbrechen" },
			{ UNDO,                     "Rückgängig" },
			{ REDO,                     "Wiederherstellen" },
			{ ACCELERATOR_OPEN,         KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK) },
			{ ACCELERATOR_SAVE,         KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK) },
			{ ACCELERATOR_ZOOM_IN,      KeyStroke.getKeyStroke('+') },
			{ ACCELERATOR_ZOOM_OUT,     KeyStroke.getKeyStroke('-') },
			{ ACCELERATOR_MOVE_UP,      KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK) },
			{ ACCELERATOR_MOVE_DOWN,    KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK) },
			{ ACCELERATOR_DELETE,       KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0) },
			{ ACCELERATOR_UNDO,         KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK) },
			{ ACCELERATOR_REDO,         KeyStroke.getKeyStroke('Y', InputEvent.CTRL_DOWN_MASK) },
			{ ABOUT_TITLE,              "Informationen" },
			{ PLUGIN_ERROR_TITLE,       "Plugin-Fehler" },
			{ PLUGIN_INIT_ERROR,        "Fehler beim Initialisieren von Plugin %s" },
			{
				PLUGIN_ERROR_VERSION_INCOMPATIBLE,
				"Dieses Plugin benötigt PdfJumbler Version %s."
			},
			{
				ABOUT_TEXT,
				"PdfJumbler %s \nCopyright (C) 2020 Martin Gropp\n" +
				"\n" +
				"PDF Editor: %s\n" +
				"PDF Renderer: %s\n" +
				"\n" +
				"Dieses Programm ist freie Software. Sie können es unter den Bedingungen\n" +
				"der GNU Affero General Public License, wie von der Free Software Foundation\n" +
				"veröffentlicht, weitergeben und/oder modifizieren, entweder gemäß\n" +
				"Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren Version.\n\n" +
				"Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es\n" +
				"Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne\n" +
				"die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR\n" +
				"EINEN BESTIMMTEN ZWECK.\n" +
				"Details finden Sie in der GNU Affero General Public License.\n\n" +
				"Sie sollten ein Exemplar der GNU Affero General Public License zusammen mit\n" +
				"diesem Programm erhalten haben. Falls nicht, siehe http://www.gnu.org/licenses/.\n" +
				"\n" +
				"Dieses Programm enthält möglicherweise (unter anderem) die folgenden Bibliotheken:\n" +
				"iText - Copyright iText Software Corp. (Lizenz: Affero GPL)\n" +
				"PdfBox - Copyright Apache Foundation (Lizenz: Apache License)\n" +
				"JPedal - Copyright IDR Solutions (Lizenz: LGPL)\n" +
				"JPod - Copyright intarsys (Lizenz: BSD style)\n" +
				"Oxygen Icons - The Oxygen Team (Lizenz: LGPL/CC by-sa)\n"
			}
		};
	}
}
