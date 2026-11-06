package io.gropp.pdfjumbler.actions;

import io.gropp.pdfjumbler.Icons;
import io.gropp.pdfjumbler.PdfJumbler;
import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;

import javax.swing.*;

public class RotateCounterClockwiseAction extends RotateAction {
	private static final long serialVersionUID = 8258949195959150573L;

	public RotateCounterClockwiseAction(PdfJumbler parent) {
		super(PdfJumblerResources.getResources().getString(I18nKeys.ROTATE_CCW), Icons.ROTATE_CCW, parent, 270);
		putValue(Action.SHORT_DESCRIPTION, PdfJumblerResources.getResources().getString(I18nKeys.ROTATE_CCW));
		putValue(Action.ACCELERATOR_KEY, PdfJumblerResources.getResources().getObject(I18nKeys.ACCELERATOR_ROTATE_CCW));
	}
}
