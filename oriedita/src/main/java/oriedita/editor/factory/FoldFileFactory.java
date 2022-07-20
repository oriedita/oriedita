package oriedita.editor.factory;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import fold.Exporter;
import fold.Importer;
import fold.impl.DefaultExporter;
import fold.impl.DefaultImporter;
import fold.model.FoldFile;
import oriedita.editor.export.Fold;

import javax.inject.Singleton;

@Module
public abstract class FoldFileFactory {
    abstract Fold fold();

    @Provides
    @Singleton
    static Importer<FoldFile> foldImport() {
        return new DefaultImporter();
    }

    @Provides
    @Singleton
    static Exporter<FoldFile> foldExport() {
        return new DefaultExporter();
    }
}
