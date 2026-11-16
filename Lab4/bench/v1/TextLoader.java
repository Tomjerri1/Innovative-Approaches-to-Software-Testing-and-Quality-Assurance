package v1;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/** Reads a text file into a string. The file harry.txt is saved in windows-1252, so the encoding is specified explicitly. */
public final class TextLoader {

    private static final Charset SOURCE_CHARSET = Charset.forName("windows-1252");

    private TextLoader() {
    }

    public static String load(Path file) throws IOException {
        return Files.readString(file, SOURCE_CHARSET);
    }
}
