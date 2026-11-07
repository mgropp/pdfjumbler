package io.gropp.pdfjumbler.i18n;

import java.util.ListResourceBundle;

public class PdfJumblerResources_es extends ListResourceBundle implements I18nKeys {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            {OPEN_DOCUMENT, "Abrir/añadir documento"},
            {SAVE_DOCUMENT_AS, "Guardar documento como"},
            {FILE_FILTER_PDF, "Documentos PDF  (*.pdf)"},
            {OVERWRITE_FILE_TITLE, "¿Sobreescribir?"},
            {OVERWRITE_FILE_TEXT, "El fichero ya existe, ¿lo desea sobreescribir?"},
            {ZOOM_OUT, "Zoom out"},
            {ZOOM_IN, "Zoom in"},
            {CLEAR_LIST, "Borrar lista"},
            {DELETE, "Eliminar"},
            {ABOUT, "Acerca de"},
            {LIST_DROP_PDFS_TO_EDIT, "¡Arrastrar documentos PDF aquí!"},
            {LIST_CLIPBOARD_EMPTY, "Portapapeles\n(vacío)"},
            {CONFIRM_CLEAR_LIST_TITLE, "Confirmar"},
            {CONFIRM_CLEAR_LIST_TEXT, "¿Eliminar todas las páginas?"},
            {NO_PDF_EDITOR_TITLE, "Error"},
            {NO_PDF_EDITOR_TEXT, "Error al cargar el plugin para editar PDF."},
            {NO_PDF_RENDERER_TITLE, "Error"},
            {NO_PDF_RENDERER_TEXT, "Error al cargar el plugin para mostrar PDF."},
            {PDF_PAGE_TITLE, "%d (%s)"},
            {MENU, "Configuración"},
            {MENU_VIEW, "Ver"},
            {MENU_EDITOR, "Plugin para editar PDF"},
            {MENU_RENDERER, "Plugin para mostrar PDF"},
            {MOVE_UP, "Mover arriba"},
            {MOVE_DOWN, "Mover abajo"},
            {PROGRESS_ABORT, "Detener"},
            {UNDO, "Deshacer"},
            {REDO, "Rehacer"},
            {ROTATE_CW, "Girar en sentido horario"},
            {ROTATE_CCW, "Girar en sentido antihorario"},
            {ABOUT_TITLE, "Acerca de"},
            {PLUGIN_ERROR_TITLE, "Error Plugin"},
            {PLUGIN_INIT_ERROR, "Error al inicializar el plugin %s"},
            {VIEW_LIST, "Lista"},
            {VIEW_THUMBNAILS, "Sólo miniaturas"},
            {PLUGIN_ERROR_VERSION_INCOMPATIBLE, "Este plugin requiere la versión %s de PdfJumbler."},
            {
                ABOUT_TEXT,
                """
                PdfJumbler %s\s
                Copyright (C) Martin Gropp
                Traducción: Andres Yesid Diaz Pinto

                PDF Editor: %s
                PDF Renderer: %s

                This program is free software; you can redistribute it and/or modify it
                under the terms of the GNU General Public License as published by the
                Free Software Foundation; either version 3 of the License, or (at your
                option) any later version.

                This program is distributed in the hope that it will be useful, but
                WITHOUT ANY WARRANTY; without even the implied warranty of
                MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
                See the GNU General Public License for more details.

                You should have received a copy of the GNU General Public License along
                with this program; if not, see http://www.gnu.org/licenses/.

                Este programa puede incluir, entre otros, las siguientes partes de software de terceros:
                PdfBox - Copyright Apache Foundation (Apache license)
                FlatLaf - Copyright FormDev Software GmbH (Apache License)
                IntelliJ IDEA CE Icons - Copyright JetBrains s.r.o. (Apache License)
                Ubuntu Mobile Icons - Copyright Canonical Ltd. (CC-BY-SA-3.0)"""
            }
        };
    }
}
