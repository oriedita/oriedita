package oriedita.editor.export.api;

import oriedita.editor.save.Save;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

public interface FileExporter extends Comparable<FileExporter> {
    default boolean supports(File file) {
        return file.getName().endsWith(getExtension());
    }

    void doExport(Save save, File file) throws IOException;

    String getName();
    String getExtension();

    /**
     * Optionally set a priority to get your exporter at the top (or bottom) of the list. A lower priority comes higher in the list.
     */
    default int getPriority() {
        return 10;
    }

    @Override
    default int compareTo(FileExporter o) {
        return Comparator.comparingInt(FileExporter::getPriority).thenComparing(FileExporter::getName)
                .compare(this, o);
    }
}
