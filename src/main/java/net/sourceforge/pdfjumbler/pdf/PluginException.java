package net.sourceforge.pdfjumbler.pdf;

public class PluginException extends Exception {
	private static final long serialVersionUID = 1L;

	public PluginException(String msg) {
		super(msg);
	}
	
	public PluginException(Throwable cause) {
		super(cause);
	}
	
	public PluginException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
