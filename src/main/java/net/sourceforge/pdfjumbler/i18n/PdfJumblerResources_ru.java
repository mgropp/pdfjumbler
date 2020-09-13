package net.sourceforge.pdfjumbler.i18n;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ListResourceBundle;

import javax.swing.KeyStroke;

/**
 * @author s-r-grass
 */
public class PdfJumblerResources_ru extends ListResourceBundle implements I18nKeys {
	@Override
	protected Object[][] getContents() {
		return new Object[][] {
			{ OPEN_DOCUMENT,            "Открыть" },
			{ SAVE_DOCUMENT,            "Сохранить" },
			{ FILE_FILTER_PDF,          "PDF документы (*.pdf)" },
			{ OVERWRITE_FILE_TITLE,     "Перезаписать?" },
			{ OVERWRITE_FILE_TEXT,      "Файл %s уже существует. Перезаписать?" },
			{ ZOOM_OUT,                 "Уменьшить" },
			{ ZOOM_IN,                  "Увеличить" },
			{ CLEAR_LIST,               "Очистить список" },
			{ DELETE,                   "Удалить" },
			{ ABOUT,                    "О программе" },
			{ LIST_DROP_PDFS_TO_EDIT,   "Перетащите сюда PDF-документы!" },
			{ LIST_CLIPBOARD_EMPTY,     "Буфер обмена \n (пусто)" },
			{ CONFIRM_CLEAR_LIST_TITLE, "Подтвердить" },
			{ CONFIRM_CLEAR_LIST_TEXT,  "Удалить все страницы?" },
			{ NO_PDF_EDITOR_TITLE,      "Ошибка" },
			{ NO_PDF_EDITOR_TEXT,       "Не удалось загрузить плагин редактора PDF." },
			{ NO_PDF_RENDERER_TITLE,    "Ошибка" },
			{ NO_PDF_RENDERER_TEXT,     "Не удалось загрузить плагин для рендеринга PDF." },
			{ PDF_PAGE_TITLE,           "%d (%s)" },
			{ MENU,                     "Настройки" },
			{ MENU_EDITOR,              "PDF Редактор" },
			{ MENU_RENDERER,            "PDF Рендер" },
			{ MOVE_UP,                  "Переместить вверх" },
			{ MOVE_DOWN,                "Переместить вниз" },
			{ PROGRESS_ABORT,           "Прервать" },
			{ UNDO,                     "Отменить" },
			{ REDO,                     "Повторить" },
			{ ROTATE_CW,                "Повернуть по часовой стрелке" },
			{ ROTATE_CCW,               "Повернуть против часовой стрелки" },
			{ ACCELERATOR_OPEN,         KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK) },
			{ ACCELERATOR_SAVE,         KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK) },
			{ ACCELERATOR_ZOOM_IN,      KeyStroke.getKeyStroke('+') },
			{ ACCELERATOR_ZOOM_OUT,     KeyStroke.getKeyStroke('-') },
			{ ACCELERATOR_MOVE_UP,      KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK) },
			{ ACCELERATOR_MOVE_DOWN,    KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK) },
			{ ACCELERATOR_DELETE,       KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0) },
			{ ACCELERATOR_UNDO,         KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK) },
			{ ACCELERATOR_REDO,         KeyStroke.getKeyStroke('Y', InputEvent.CTRL_DOWN_MASK) },
			{ ABOUT_TITLE,              "О программе" },
			{ PLUGIN_ERROR_TITLE,       "Ошибка плагина" },
			{ PLUGIN_INIT_ERROR,        "Ошибка инициализации плагина %s" },
			{
				PLUGIN_ERROR_VERSION_INCOMPATIBLE,
				"Для этого плагина требуется версия PdfJumbler %s."
			},
			{
				ABOUT_TEXT,
				"PdfJumbler %s \nАвторские права (C) 2020 Мартин Гропп\n" +
				"Перевод: s-r-grass\n" +
				"\n" +
				"PDF Редактор: %s\n" +
				"PDF Рендер: %s\n" +
				"\n" +
				"Эта программа - свободное ПО; вы можете распространять и/или изменять\n" +
				"в соответствии с условиями Стандартной общественной лицензии GNU Affero, опубликованной\n" +
				"Фондом Свободного Программного обеспечения; либо версии 3 Лицензии, либо (на ваш\n" +
				"выбор) любой более поздней версии.\n\n" +
				"Эта программа распространяется в надежде, что она будет полезна, но\n" +
				"БЕЗ КАКИХ-ЛИБО ГАРАНТИЙ; даже без подразумеваемой гарантии\n" +
				"КОММЕРЧЕСКОЙ ЦЕННОСТИ или ПРИГОДНОСТИ ДЛЯ ОПРЕДЕЛЕННОЙ ЦЕЛИ.\n" +
				"Для подробностей смотрите GNU Affero General Public License.\n\n" +
				"Вы должны были получить копию Стандартной общественной лицензии GNU Affero вместе с\n" +
				"с этой программой; если нет, смотри http://www.gnu.org/licenses/.\n" +
				"\n" +
				"Это программное обеспечение может включать (среди прочего) следующие сторонние программы:\n" +
				"PdfBox - Авторские права Apache Foundation (Apache license)\n" +
				"Oxygen Icons - The Oxygen Team (LGPL/CC by-sa)\n"
			}
		};
	}
}
