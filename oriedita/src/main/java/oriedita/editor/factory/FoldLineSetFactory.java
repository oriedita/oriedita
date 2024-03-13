package oriedita.editor.factory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import origami.crease_pattern.FoldLineSet;

/**
 * Provides specific named fold line sets
 */
public class FoldLineSetFactory {
    @Named("auxlines")
    @Produces
    @ApplicationScoped
    FoldLineSet auxLinesFoldLineSet() {
        return new FoldLineSet();
    }

    @Named("foldlines")
    @Produces
    @ApplicationScoped
    FoldLineSet foldLineSet() {
        return new FoldLineSet();
    }

    @Named("backup_auxlines")
    @Produces
    @ApplicationScoped
    FoldLineSet backupAuxLinesFoldLineSet() {
        return new FoldLineSet();
    }

    @Named("backup_foldlines")
    @Produces
    @ApplicationScoped
    FoldLineSet backupFoldLineSet() {
        return new FoldLineSet();
    }
}
