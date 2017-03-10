package net.sourceforge.pdfjumbler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pdfjumbler.pdf.Page;
import net.sourceforge.pdfjumbler.pdf.PdfProcessingFactory;

/**
 * Creates & keeps track of open pages.
 * 
 * @author Martin Gropp <martin.gropp@googlemail.com>
 */
public class DocumentManager {
	private static Map<File,List<Page>> pageMap = new HashMap<File,List<Page>>();
	
	public static List<Page> getPages(File file) throws IOException {
		if (pageMap.containsKey(file)) {
			return pageMap.get(file);
		}
		
		int pageCount = PdfProcessingFactory.getRenderer().getNumberOfPages(file);
		List<Page> pages = new ArrayList<Page>(pageCount);
		for (int i = 0; i < pageCount; i++) {
			Page page = new Page(file, i);
			pages.add(page);
		}
		
		pages = Collections.unmodifiableList(pages);
		pageMap.put(file, pages);
		
		return pages;
	}
	
	public static Collection<File> getAllFiles() {
		return Collections.unmodifiableSet(pageMap.keySet());
	}
	
	public static Collection<Page> getAllPages() {
		ArrayList<Page> pages = new ArrayList<Page>();
		for (List<Page> pageList : pageMap.values()) {
			pages.addAll(pageList);
		}
		
		return pages;
	}
}
