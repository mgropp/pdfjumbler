package io.gropp.pdfjumbler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import io.gropp.pdfjumbler.pdf.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DocumentManagerTest {
	@TempDir
	File tempDir;

	private final List<File> countedFiles = new ArrayList<>();
	private final DocumentManager manager = new DocumentManager(file -> {
		countedFiles.add(file);
		return 3;
	});

	@Test
	void createsOnePagePerPageOfTheFile() throws IOException {
		File file = new File(tempDir, "doc.pdf");

		List<Page> pages = manager.getPages(file);

		assertThat(pages).containsExactly(new Page(file, 0), new Page(file, 1), new Page(file, 2));
	}

	@Test
	void returnsSamePagesForSameFile() throws IOException {
		File file = new File(tempDir, "doc.pdf");

		List<Page> first = manager.getPages(file);
		List<Page> second = manager.getPages(file);

		assertThat(second).isSameAs(first);
		assertThat(countedFiles).containsExactly(file);
	}

	@Test
	void returnedPagesAreUnmodifiable() throws IOException {
		List<Page> pages = manager.getPages(new File(tempDir, "doc.pdf"));

		assertThatThrownBy(() -> pages.remove(0)).isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	void propagatesPageCountFailure() {
		DocumentManager failing = new DocumentManager(file -> {
			throw new IOException("broken");
		});

		assertThatThrownBy(() -> failing.getPages(new File(tempDir, "doc.pdf"))).hasMessage("broken");
		assertThat(failing.getAllFiles()).isEmpty();
	}

	@Test
	void tracksOpenedFiles() throws IOException {
		File first = new File(tempDir, "a.pdf");
		File second = new File(tempDir, "b.pdf");
		manager.getPages(first);
		manager.getPages(second);

		assertThat(manager.getAllFiles()).containsExactlyInAnyOrder(first, second);
	}

	@Test
	void moveAsideIgnoresFilesThatAreNotOpen() throws IOException {
		File file = new File(tempDir, "doc.pdf");
		Files.writeString(file.toPath(), "content");

		manager.moveAsideIfOpen(file);

		assertThat(file).hasContent("content");
	}

	@Test
	void moveAsideIgnoresOpenFilesThatNoLongerExist() throws IOException {
		File file = new File(tempDir, "doc.pdf");
		List<Page> pages = manager.getPages(file);

		manager.moveAsideIfOpen(file);

		assertThat(pages).allSatisfy(page -> assertThat(page.getFile()).isEqualTo(file));
	}
}
