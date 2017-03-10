package net.sourceforge.pdfjumbler.jdragdroplist;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.TransferHandler.TransferSupport;

/**
 * @author Martin Gropp <martin.gropp@googlemail.com>
 */
public class DropUtil {
	public final static DataFlavor DATA_FLAVOR_URILIST_STRING;

	static {
		try {
			DATA_FLAVOR_URILIST_STRING = new DataFlavor("text/uri-list;class=java.lang.String");
		}
		catch (ClassNotFoundException e) {
			throw new AssertionError(e);
		}
	}
	
	public static boolean isListItemDrop(TransferSupport info) {
		return info.isDataFlavorSupported(JDDLTransferData.DATA_FLAVOR);
	}
	
	public static boolean isURIDrop(TransferSupport info) {
		return
			info.isDataFlavorSupported(DataFlavor.javaFileListFlavor) ||
			info.isDataFlavorSupported(DATA_FLAVOR_URILIST_STRING);
	}
	
	@SuppressWarnings("unchecked")
	public static List<URI> getURIs(TransferSupport info) {
		if (info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			List<File> data;
			try {
				data = (List<File>)info.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
			}
			catch (UnsupportedFlavorException e) {
				throw new AssertionError(e);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			List<URI> uriList = new ArrayList<URI>(data.size());
			for (File file : data) {
				uriList.add(file.toURI());
				System.err.println(file);
			}
			
			return uriList;
		} else if (info.isDataFlavorSupported(DATA_FLAVOR_URILIST_STRING)) {
			String data;
			try {
				data = (String)info.getTransferable().getTransferData(DATA_FLAVOR_URILIST_STRING);
			}
			catch (UnsupportedFlavorException e) {
				throw new AssertionError(e);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			String[] uris = data.split("\n");
			List<URI> uriList = new ArrayList<URI>(uris.length);
			
			for (String uri : uris) {
				uri = uri.trim();
				if (uri.length() == 0) {
					continue;
				}
				
				try {
					uriList.add(new URI(uri));
				}
				catch (URISyntaxException e) {
					System.err.println("WARNING: " + e);
				}
			}
			
			return uriList;
		}
		
		return null;
	}
}
