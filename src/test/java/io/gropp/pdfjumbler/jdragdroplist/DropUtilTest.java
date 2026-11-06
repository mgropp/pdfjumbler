package io.gropp.pdfjumbler.jdragdroplist;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.net.URI;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.TransferHandler.TransferSupport;
import org.junit.jupiter.api.Test;

class DropUtilTest {
    private static TransferSupport support(DataFlavor flavor, Object data) {
        Transferable transferable = new Transferable() {
            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] {flavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor candidate) {
                return flavor.equals(candidate);
            }

            @Override
            public Object getTransferData(DataFlavor candidate) {
                return data;
            }
        };
        return new TransferSupport(new JPanel(), transferable);
    }

    @Test
    void convertsFileListToUris() {
        File file = new File("/docs/a.pdf");

        List<URI> uris = DropUtil.getURIs(support(DataFlavor.javaFileListFlavor, List.of(file)));

        assertThat(uris).containsExactly(file.toURI());
    }

    @Test
    void parsesUriListSkippingBlankLines() {
        String data = "file:///docs/a.pdf\r\n\r\nfile:///docs/b.pdf\r\n";

        List<URI> uris = DropUtil.getURIs(support(DropUtil.DATA_FLAVOR_URILIST_STRING, data));

        assertThat(uris).containsExactly(URI.create("file:///docs/a.pdf"), URI.create("file:///docs/b.pdf"));
    }

    @Test
    void skipsInvalidUris() {
        String data = "file:///docs/a.pdf\nnot a uri\n";

        List<URI> uris = DropUtil.getURIs(support(DropUtil.DATA_FLAVOR_URILIST_STRING, data));

        assertThat(uris).containsExactly(URI.create("file:///docs/a.pdf"));
    }

    @Test
    void skipsUriListComments() {
        String data = "#dropped-by-file-manager\r\nfile:///docs/a.pdf\r\n";

        List<URI> uris = DropUtil.getURIs(support(DropUtil.DATA_FLAVOR_URILIST_STRING, data));

        assertThat(uris).containsExactly(URI.create("file:///docs/a.pdf"));
    }

    @Test
    void recognizesUriDrops() {
        assertThat(DropUtil.isURIDrop(support(DataFlavor.javaFileListFlavor, List.of())))
                .isTrue();
        assertThat(DropUtil.isURIDrop(support(DropUtil.DATA_FLAVOR_URILIST_STRING, "")))
                .isTrue();
        assertThat(DropUtil.isURIDrop(support(DataFlavor.stringFlavor, ""))).isFalse();
    }

    @Test
    void returnsNullForUnsupportedData() {
        StringSelection selection = new StringSelection("text");

        assertThat(DropUtil.getURIs(new TransferSupport(new JPanel(), selection)))
                .isNull();
    }
}
