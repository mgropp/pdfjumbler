package net.sourceforge.pdfjumbler.i18n;

import java.util.ListResourceBundle;

/**
 * @author s-r-grass
 */
public class PdfJumblerResources_ru extends ListResourceBundle implements I18nKeys {
	@Override
	protected Object[][] getContents() {
		return new Object[][] {
			{ OPEN_DOCUMENT,            "Открыть/Добавить" },
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
			{ MENU_VIEW,                "Вид" },
			{ MENU_EDITOR,              "PDF Редактор" },
			{ MENU_RENDERER,            "PDF Рендер" },
			{ MOVE_UP,                  "Переместить вверх" },
			{ MOVE_DOWN,                "Переместить вниз" },
			{ PROGRESS_ABORT,           "Прервать" },
			{ UNDO,                     "Отменить" },
			{ REDO,                     "Повторить" },
			{ ROTATE_CW,                "Повернуть по часовой стрелке" },
			{ ROTATE_CCW,               "Повернуть против часовой стрелки" },
			{ ABOUT_TITLE,              "О программе" },
			{ PLUGIN_ERROR_TITLE,       "Ошибка плагина" },
			{ PLUGIN_INIT_ERROR,        "Ошибка инициализации плагина %s" },
			{ VIEW_LIST,                "Ведомость" },
			{ VIEW_THUMBNAILS,          "Эскиз" },
			{
				PLUGIN_ERROR_VERSION_INCOMPATIBLE,
				"Для этого плагина требуется версия PdfJumbler %s."
			},
			{
				ABOUT_TEXT,
				"PdfJumbler %s \nАвторские права (C) 2021 Мартин Гропп\n" +
				"Перевод: s-r-grass\n" +
				"\n" +
				"PDF Редактор: %s\n" +
				"PDF Рендер: %s\n" +
				"\n" +
				"Эта программа - свободное ПО; вы можете распространять и/или изменять\n" +
				"в соответствии с условиями Стандартной общественной лицензии GNU, опубликованной\n" +
				"Фондом Свободного Программного обеспечения; либо версии 3 Лицензии, либо (на ваш\n" +
				"выбор) любой более поздней версии.\n\n" +
				"Эта программа распространяется в надежде, что она будет полезна, но\n" +
				"БЕЗ КАКИХ-ЛИБО ГАРАНТИЙ; даже без подразумеваемой гарантии\n" +
				"КОММЕРЧЕСКОЙ ЦЕННОСТИ или ПРИГОДНОСТИ ДЛЯ ОПРЕДЕЛЕННОЙ ЦЕЛИ.\n" +
				"Для подробностей смотрите GNU General Public License.\n\n" +
				"Вы должны были получить копию Стандаplртной общественной лицензии GNU вместе с\n" +
				"с этой программой; если нет, смотри http://www.gnu.org/licenses/.\n" +
				"\n" +
				"Это программное обеспечение может включать (среди прочего) следующие сторонние программы:\n" +
				"PdfBox - Авторские права Apache Foundation (Apache license)\n" +
				"FlatLaf - Авторские права FormDev Software GmbH (Apache License)\n" +
				"IntelliJ IDEA CE Icons - Авторские права JetBrains s.r.o. (Apache License)\n" +
				"Ubuntu Mobile Icons - Авторские права Canonical Ltd. (CC-BY-SA-3.0)"
			}
		};
	}
}
