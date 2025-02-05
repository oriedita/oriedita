package oriedita.editor.export;

import fold.io.CreasePatternWriter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tinylog.Logger;
import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.export.api.FileExporter;
import oriedita.editor.save.Save;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

@ApplicationScoped
public class CpExporter implements FileExporter {
    private final FrameProvider frame;
    private final ApplicationModel applicationModel;

    @Inject
    public CpExporter(FrameProvider frame, ApplicationModel applicationModel) {
        this.frame = frame;
        this.applicationModel = applicationModel;
    }

    @Override
    public void doExport(Save save, File file) throws IOException {
        if (!save.canSaveAsCp() && !applicationModel.getCpExportWarning()) {
            JCheckBox checkbox = new JCheckBox("Don't show this again");
            Object[] params = {"The saved .cp file does not contain circles, text and yellow aux lines. Save as a .ori file to also save these lines.", checkbox};
            JOptionPane.showMessageDialog(frame.get(), params, "Warning", JOptionPane.WARNING_MESSAGE);

            applicationModel.setCpExportWarning(checkbox.isSelected());
        }

        try (FileWriter fw = new FileWriter(file); BufferedWriter bw = new BufferedWriter(fw); PrintWriter pw = new PrintWriter(bw); OutputStream os = new FileOutputStream(file)) {
            CreasePatternWriter creasePatternWriter = new CreasePatternWriter(os);
            creasePatternWriter.write(new FoldExporter().toFoldSave(save));
        } catch (InterruptedException e) {
            Logger.error(e, "Error exporting cp file");
        }
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
