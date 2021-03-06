package net.sourceforge.pdfjumbler.i18n;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;

import javax.swing.KeyStroke;

/**
 * @author Martin Gropp
 */
public class PdfJumblerResources extends ListResourceBundle implements I18nKeys {
	private static final ResourceBundle resources = ResourceBundle.getBundle(PdfJumblerResources.class.getCanonicalName());

	public static ResourceBundle getResources() {
		return resources;
	}

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
			{ OPEN_DOCUMENT,              "Open/add document" },
			{ SAVE_DOCUMENT,              "Save document" },
			{ FILE_FILTER_PDF,            "PDF documents (*.pdf)" },
			{ OVERWRITE_FILE_TITLE,       "Overwrite?" },
			{ OVERWRITE_FILE_TEXT,        "The file %s already exists. Overwrite?" },
			{ ZOOM_OUT,                   "Zoom out" },
			{ ZOOM_IN,                    "Zoom in" },
			{ CLEAR_LIST,                 "Clear list" },
			{ DELETE,                     "Delete" },
			{ ABOUT,                      "About" },
			{ LIST_DROP_PDFS_TO_EDIT,     "Drag PDF documents here!" },
			{ LIST_CLIPBOARD_EMPTY,       "Clipboard\n(empty)" },
			{ CONFIRM_CLEAR_LIST_TITLE,   "Confirm" },
			{ CONFIRM_CLEAR_LIST_TEXT,    "Delete all pages?" },
			{ NO_PDF_EDITOR_TITLE,        "Error" },
			{ NO_PDF_EDITOR_TEXT,         "Failed to load a PDF editor plugin." },
			{ NO_PDF_RENDERER_TITLE,      "Error" },
			{ NO_PDF_RENDERER_TEXT,       "Failed to load a PDF renderer plugin." },
			{ PDF_PAGE_TITLE,             "%d (%s)" },
			{ MENU,                       "Menu" },
			{ MENU_VIEW,                  "View" },
			{ MENU_EDITOR,                "PDF Editor" },
			{ MENU_RENDERER,              "PDF Renderer" },
			{ MOVE_UP,                    "Move up" },
			{ MOVE_DOWN,                  "Move down" },
			{ PROGRESS_ABORT,             "Abort" },
			{ UNDO,                       "Undo" },
			{ REDO,                       "Redo" },
			{ ROTATE_CW,                  "Rotate clockwise" },
			{ ROTATE_CCW,                 "Rotate counter-clockwise" },
			{ ACCELERATOR_OPEN,           KeyStroke.getKeyStroke('O', I18nUtil.MENU_SHORTCUT_KEY) },
			{ ACCELERATOR_SAVE,           KeyStroke.getKeyStroke('S', I18nUtil.MENU_SHORTCUT_KEY) },
			{ ACCELERATOR_ZOOM_IN,        KeyStroke.getKeyStroke('+') },
			{ ACCELERATOR_ZOOM_OUT,       KeyStroke.getKeyStroke('-') },
			{ ACCELERATOR_MOVE_UP,        KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK) },
			{ ACCELERATOR_MOVE_DOWN,      KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK) },
			{ ACCELERATOR_DELETE,         KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0) },
			{ ACCELERATOR_UNDO,           KeyStroke.getKeyStroke('Z', I18nUtil.MENU_SHORTCUT_KEY) },
			{ ACCELERATOR_REDO,           KeyStroke.getKeyStroke('Y', I18nUtil.MENU_SHORTCUT_KEY) },
			{ ACCELERATOR_ROTATE_CW,      KeyStroke.getKeyStroke('R', I18nUtil.MENU_SHORTCUT_KEY) },
			{ ACCELERATOR_ROTATE_CCW,     KeyStroke.getKeyStroke('R', I18nUtil.MENU_SHORTCUT_KEY | InputEvent.SHIFT_DOWN_MASK) },
			{ ABOUT_TITLE,                "About" },
			{ PLUGIN_ERROR_TITLE,         "Plugin Error" },
			{ PLUGIN_INIT_ERROR,          "Error initializing plugin %s" },
			{ VIEW_LIST,                  "List" },
			{ VIEW_THUMBNAILS,            "Thumbnails only" },
			{
				PLUGIN_ERROR_VERSION_INCOMPATIBLE,
				"This plugin requires PdfJumbler version %s."
			},
			{
				ABOUT_TEXT,
				"PdfJumbler %s \nCopyright (C) 2021 Martin Gropp\n" +
				"\n" +
				"PDF Editor: %s\n" +
				"PDF Renderer: %s\n" +
				"\n" +
				"This program is free software; you can redistribute it and/or modify it\n" +
				"under the terms of the GNU General Public License as published by the\n" +
				"Free Software Foundation; either version 3 of the License, or (at your\n" +
				"option) any later version.\n\n" +
				"This program is distributed in the hope that it will be useful, but\n" +
				"WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
				"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.\n" +
				"See the GNU General Public License for more details.\n\n" +
				"You should have received a copy of the GNU General Public License along\n" +
				"with this program; if not, see http://www.gnu.org/licenses/.\n" +
				"\n" +
				"This software may include (among others) the following third party content:\n" +
				"PdfBox - Copyright Apache Foundation (Apache License)\n" +
				"FlatLaf - Copyright FormDev Software GmbH (Apache License)\n" +
				"IntelliJ IDEA CE Icons - Copyright JetBrains s.r.o. (Apache License)\n" +
				"Ubuntu Mobile Icons - Copyright Canonical Ltd. (CC-BY-SA-3.0)"
			}
		};
	}
}
