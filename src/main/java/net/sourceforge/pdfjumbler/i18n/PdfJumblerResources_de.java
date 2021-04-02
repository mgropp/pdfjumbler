package net.sourceforge.pdfjumbler.i18n;

import java.util.ListResourceBundle;

/**
 * @author Martin Gropp
 */
public class PdfJumblerResources_de extends ListResourceBundle implements I18nKeys {
	@Override
	protected Object[][] getContents() {
		return new Object[][] {
			{ OPEN_DOCUMENT,            "Dokument öffnen/hinzufügen" },
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
			{ MENU_VIEW,                "Ansicht" },
			{ MENU_EDITOR,              "PDF-Editor" },
			{ MENU_RENDERER,            "PDF-Anzeige" },
			{ PROGRESS_ABORT,           "Abbrechen" },
			{ UNDO,                     "Rückgängig" },
			{ REDO,                     "Wiederherstellen" },
			{ ROTATE_CW,                "Drehen (im Uhrzeigersinn)" },
			{ ROTATE_CCW,               "Drehen (gegen den Uhrzeigersinn)" },
			{ ABOUT_TITLE,              "Informationen" },
			{ PLUGIN_ERROR_TITLE,       "Plugin-Fehler" },
			{ PLUGIN_INIT_ERROR,        "Fehler beim Initialisieren von Plugin %s" },
			{ VIEW_LIST,                "Liste" },
			{ VIEW_THUMBNAILS,          "Nur Miniaturansichten" },
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
				"der GNU General Public License, wie von der Free Software Foundation\n" +
				"veröffentlicht, weitergeben und/oder modifizieren, entweder gemäß\n" +
				"Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren Version.\n\n" +
				"Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, daß es\n" +
				"Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne\n" +
				"die implizite Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR\n" +
				"EINEN BESTIMMTEN ZWECK.\n" +
				"Details finden Sie in der GNU General Public License.\n\n" +
				"Sie sollten ein Exemplar der GNU General Public License zusammen mit\n" +
				"diesem Programm erhalten haben. Falls nicht, siehe http://www.gnu.org/licenses/.\n" +
				"\n" +
				"Dieses Programm enthält möglicherweise (unter anderem) die folgenden Inhalte:\n" +
				"PdfBox - Copyright Apache Foundation (Apache License)\n" +
				"FlatLaf - Copyright FormDev Software GmbH (Apache License)\n" +
				"IntelliJ IDEA CE Icons - Copyright JetBrains s.r.o. (Apache License)\n" +
				"Ubuntu Mobile Icons - Copyright Canonical Ltd. (CC-BY-SA-3.0)"
			}
		};
	}
}
