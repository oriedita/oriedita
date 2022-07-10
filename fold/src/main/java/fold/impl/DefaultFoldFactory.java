package fold.impl;

import fold.Exporter;
import fold.FoldFactory;
import fold.Importer;

public class DefaultFoldFactory implements FoldFactory {
    private Importer anImporter;
    private Exporter exporter;

    private void createFoldImport() {
        anImporter = new DefaultImporter();
    }

    private void createFoldExport() {
        exporter = new DefaultExporter();
    }

    @Override
    public Importer foldImport() {
        if (anImporter == null) {
            createFoldImport();
        }
        return anImporter;
    }

    @Override
    public Exporter foldExport() {
        if (exporter == null) {
            createFoldExport();
        }
        return exporter;
    }
}
