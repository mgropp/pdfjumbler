package io.gropp.pdfjumbler.ui;

import com.formdev.flatlaf.util.UIScale;
import io.gropp.pdfjumbler.i18n.I18nKeys;
import io.gropp.pdfjumbler.i18n.PdfJumblerResources;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.jspecify.annotations.Nullable;

public class ProgressDialog extends JDialog {
    @Serial
    private static final long serialVersionUID = 5790932640733135316L;

    private final SwingWorker<?, ?> worker;
    private final JProgressBar progressBar;
    private final JLabel statusLabel;

    public ProgressDialog(@Nullable Frame parent, SwingWorker<?, ?> worker) {
        super(parent, true);
        this.worker = worker;

        setSize(UIScale.scale(300), UIScale.scale(120));
        setLocationRelativeTo(parent);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.insets = UIScale.scale(new Insets(4, 4, 4, 4));

        statusLabel = new JLabel();
        add(statusLabel, c);

        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        progressBar.setIndeterminate(true);
        progressBar.setEnabled(true);
        add(progressBar, c);

        JButton abortButton = new JButton(
                new AbstractAction(PdfJumblerResources.getResources().getString(I18nKeys.PROGRESS_ABORT)) {
                    @Serial
                    private static final long serialVersionUID = -8203887045883710945L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        abort();
                    }
                });
        c.fill = GridBagConstraints.NONE;
        add(abortButton, c);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                abort();
            }
        });

        worker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent e) {
                if (e.getPropertyName().equals("state") && e.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ProgressDialog.this.setVisible(false);
                            ProgressDialog.this.dispose();
                        }
                    });
                } else if (e.getPropertyName().equals("note")) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            statusLabel.setText((String) e.getNewValue());
                        }
                    });
                } else if (e.getPropertyName().equals("progress")) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if ((Integer) e.getNewValue() >= 0) {
                                progressBar.setIndeterminate(false);
                                progressBar.setValue((Integer) e.getNewValue());
                            }
                        }
                    });
                }
            }
        });
    }

    public void abort() {
        if (!worker.isDone()) {
            worker.cancel(true);
        }
        setVisible(false);
        dispose();
    }

    public static boolean run(SwingWorker<?, ?> worker, @Nullable Frame parent) throws Exception {
        ProgressDialog dialog = new ProgressDialog(parent, worker);
        worker.execute();
        dialog.setVisible(true);
        try {
            worker.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof CancellationException) {
                return false;
            } else if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            } else {
                // ?!?
                throw new AssertionError(e);
            }
        }

        return !worker.isCancelled();
    }
}
