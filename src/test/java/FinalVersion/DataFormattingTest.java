package FinalVersion;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataFormattingTest {

    private final DataFormatting df = new DataFormatting();

    @Test
    public void test_formatJson_validInput() {
        String json = "{\"files\": [{\"filename\": \"src/file1.txt\"}, {\"filename\": \"src/file2.txt\"}]}";
        List<String> files = df.formatJson(json);
        assertEquals(2, files.size());
        assertEquals("src/file1.txt", files.get(0));
        assertEquals("src/file2.txt", files.get(1));
    }

    @Test
    public void test_formatJson_invalidInput() {
        String json = "someInvalidInputForJson";
        assertThrows(RuntimeException.class, () -> df.formatJson(json));
    }

    @Test
    public void test_formatJson_emptyFilesInput() {
        String json = "{\"files\": []}";
        List<String> files = df.formatJson(json);
        assertTrue(files.isEmpty());
    }

    @Test
    public void test_formatJson_emptyInput() {
        String json = "";
        List<String> files = df.formatJson(json);
        assertTrue(files.isEmpty());
    }

    @Test
    public void test_formatJson_nullInput() {
        String json = null;
        assertThrows(RuntimeException.class, () -> df.formatJson(json));
    }

    @Test
    public void test_formatJson_filesNodeNotArray() {
        String json = "{\"files\": \"notAnArray\"}";
        List<String> files = df.formatJson(json);
        assertTrue(files.isEmpty());
    }

    @Test
    public void test_formatJson_missingFilesNode() {
        String json = "{\"otherKey\": []}";
        List<String> files = df.formatJson(json);
        assertTrue(files.isEmpty());
    }

    @Test
    public void test_formatJson_filenameMissingInFile() {
        String json = "{\"files\": [{}]}";
        List<String> files = df.formatJson(json);
        assertEquals(1, files.size());
        assertEquals("", files.getFirst());
    }

    @Test
    public void test_formatJson_filenameNull() {
        String json = "{\"files\": [{\"filename\": null}]}";
        List<String> files = df.formatJson(json);
        assertEquals(1, files.size());
        assertEquals("null", files.getFirst());
    }
}