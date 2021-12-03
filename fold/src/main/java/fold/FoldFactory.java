package fold;

import fold.impl.FoldExportImpl;
import fold.impl.FoldFileProcessorImpl;
import fold.impl.FoldImportImpl;

public class FoldFactory {
    private static FoldImport foldImport;
    private static FoldExport foldExport;
    private static FoldFileProcessor processor;

    private static void createProcessor() {
        processor = new FoldFileProcessorImpl();
    }

    private static void createFoldImport() {
        if (processor == null) {
            createProcessor();
        }
        foldImport = new FoldImportImpl(processor);
    }

    private static void createFoldExport() {
        if (processor == null) {
            createProcessor();
        }
        foldExport = new FoldExportImpl(processor);
    }

    public static FoldImport foldImport() {
        if (foldImport == null) {
            createFoldImport();
        }
        return foldImport;
    }

    public static FoldExport foldExport() {
        if (foldExport == null) {
            createFoldExport();
        }
        return foldExport;
    }
}
