package net.sourceforge.pdfjumbler;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public final class Icons {
	private static final int STANDARD_BASE_SIZE = 22;
	private static final int SMALL_BASE_SIZE = 16;
	private static final int APP_ICON_BASE_SIZE = 64;
	private static final double[] FACTORS = { 1.0, 1.25, 1.5, 1.75, 2.0, 2.25, 2.5, 2.75, 3.0 };

	public static final ImageIcon PDF_JUMBLER = createMultiResIcon("pdfjumbler.svg", APP_ICON_BASE_SIZE);

	public static final ImageIcon ZOOM_IN = createMultiResIcon("zoom-in.svg", STANDARD_BASE_SIZE);
	public static final ImageIcon ZOOM_OUT = createMultiResIcon("zoom-out.svg", STANDARD_BASE_SIZE);
	public static final ImageIcon DOCUMENT_OPEN = createMultiResIcon("document-open.svg", STANDARD_BASE_SIZE);
	public static final ImageIcon DOCUMENT_SAVE = createMultiResIcon("document-save.svg", STANDARD_BASE_SIZE);
	public static final ImageIcon EDIT_DELETE = createMultiResIcon("edit-delete.svg", STANDARD_BASE_SIZE);
	public static final ImageIcon MENU = createMultiResIcon("menu.svg", STANDARD_BASE_SIZE);
	public static final ImageIcon ROTATE_CW = createMultiResIcon("rotate-cw.svg", STANDARD_BASE_SIZE);
	public static final ImageIcon ROTATE_CCW = createMultiResIcon("rotate-ccw.svg", STANDARD_BASE_SIZE);

	public interface Size16 {
		ImageIcon DOCUMENT_OPEN = createMultiResIcon("document-open.svg", SMALL_BASE_SIZE);
		ImageIcon DOCUMENT_SAVE = createMultiResIcon("document-save.svg", SMALL_BASE_SIZE);
	}

	private static ImageIcon createMultiResIcon(String svgResourceName, int baseSize) {
		List<Image> images = new ArrayList<>(FACTORS.length);
		ImageTranscoder transcoder = new ImageTranscoder() {
			@Override
			public BufferedImage createImage(int width, int height) {
				return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			}

			@Override
			public void writeImage(BufferedImage image, TranscoderOutput output) {
				images.add(image);
			}
		};

		TranscodingHints hints = transcoder.getTranscodingHints();
		for (double factor : FACTORS) {
			hints.put(
				ImageTranscoder.KEY_MAX_WIDTH,
				(float)(factor * baseSize)
			);

			try (InputStream stream = Icons.class.getClassLoader().getResourceAsStream(svgResourceName)) {
				TranscoderInput input = new TranscoderInput(stream);
				transcoder.setTranscodingHints(hints);
				transcoder.transcode(input, null);
			}
			catch (IOException | TranscoderException e) {
				throw new RuntimeException(e);
			}
		}

		return new ImageIcon(new BaseMultiResolutionImage(images.toArray(new Image[0])));
	}
}
