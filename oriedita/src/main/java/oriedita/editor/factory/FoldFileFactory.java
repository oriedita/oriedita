package oriedita.editor.factory;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import fold.Exporter;
import fold.Importer;
import fold.impl.CustomImporter;
import fold.impl.DefaultExporter;
import oriedita.editor.export.Fold;
import oriedita.editor.save.OrieditaFoldFile;

import javax.inject.Singleton;

@Module
public abstract class FoldFileFactory {
    abstract Fold fold();

    @Provides
    @Singleton
    static Importer<OrieditaFoldFile> foldImport() {
        return new CustomImporter<>(OrieditaFoldFile.class);
    }

    @Provides
    @Singleton
    static Exporter<OrieditaFoldFile> foldExport() {
        return new DefaultExporter<>();
    }
}
