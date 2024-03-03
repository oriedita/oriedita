package oriedita.editor.export;

import fold.io.CreasePatternReader;
import fold.io.CreasePatternWriter;
import fold.model.Edge;
import fold.model.FoldFile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.FrameProvider;
import oriedita.editor.save.Save;
import oriedita.editor.save.SaveProvider;
import oriedita.filesupport.api.FileExporter;
import oriedita.filesupport.api.FileImporter;
import origami.crease_pattern.element.LineSegment;
import origami.crease_pattern.element.Point;

import javax.swing.JOptionPane;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

@ApplicationScoped
public class CpExporter implements FileImporter, FileExporter {
    private final FrameProvider frame;

    @Inject
    public CpExporter(FrameProvider frame) {
        this.frame = frame;
    }

    public static void exportFile(Save save, File file) throws IOException {
        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw); PrintWriter pw = new PrintWriter(bw); OutputStream os = new FileOutputStream(file)) {
            CreasePatternWriter creasePatternWriter = new CreasePatternWriter(os);
            creasePatternWriter.write(new FoldExporter().toFoldSave(save));
        } catch (InterruptedException e) {
            Logger.error(e, "Error exporting cp file");
        }
    }

    public static Save importFile(InputStream is) throws IOException {
        Save save = SaveProvider.createInstance();

        CreasePatternReader creasePatternReader = new CreasePatternReader(is);

        FoldFile foldFile = creasePatternReader.read();

        for (Edge edge : foldFile.getRootFrame().getEdges()) {
            save.addLineSegment(new LineSegment(new Point(edge.getStart().getX(), edge.getStart().getY()), new Point(edge.getEnd().getX(), edge.getEnd().getY()), FoldExporter.getColor(edge.getAssignment())));
        }

        return save;
    }

    public static Save importFile(File mem) throws IOException {
        try (FileInputStream is = new FileInputStream(mem)) {
            return importFile(is);
        }
    }

    @Override
    public boolean supports(File filename) {
        return FileExporter.super.supports(filename);
    }

    @Override
    public void doExport(Save save, File file) throws IOException {
        if (!save.canSaveAsCp()) {
            JOptionPane.showMessageDialog(frame.get(), "The saved .cp file does not contain circles, text and yellow aux lines. Save as a .ori file to also save these lines.", "Warning", JOptionPane.WARNING_MESSAGE);
        }

        exportFile(save, file);
    }

    @Override
    public Save doImport(File file) throws IOException {
        return importFile(file);
    }

    @Override
    public String getName() {
        return "Crease Pattern";
    }

    @Override
    public String getExtension() {
        return ".cp";
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
