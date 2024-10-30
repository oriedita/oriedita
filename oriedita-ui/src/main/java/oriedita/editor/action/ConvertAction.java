package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.FrameProvider;
import oriedita.editor.databinding.ApplicationModel;
import oriedita.editor.export.CpExporter;
import oriedita.editor.export.CpImporter;
import oriedita.editor.export.FoldExporter;
import oriedita.editor.export.FoldImporter;
import oriedita.editor.export.OrhExporter;
import oriedita.editor.export.OrhImporter;
import oriedita.editor.export.OriExporter;
import oriedita.editor.export.OriImporter;
import oriedita.editor.save.Save;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;

@ApplicationScoped
@ActionHandler(ActionType.convertAction)
public class ConvertAction extends AbstractOrieditaAction{
    @Inject
    FrameProvider frameProvider;
    @Inject
    ApplicationModel applicationModel;
    private String selectedOption;
    List<String> allowedExtensions = Arrays.asList(".cp", ".ori", ".orh", ".fold");
    String[] options = {"Crease Pattern (.cp)", "Ori (.ori)", "Orihime (.orh)", "FOLD (.fold)"};

    @Inject
    public ConvertAction(){}

    @Override
    public void actionPerformed(ActionEvent e) {
        File[] selectedFiles;
        if((selectedFiles = getSelectedFiles()).length == 0) return;

        chooseExportExtension();

        processSelectedFiles(selectedFiles);

        applicationModel.setDefaultDirectory(selectedFiles[0].getParent());
    }

    private File[] getSelectedFiles(){
        FileDialog fd = new FileDialog(frameProvider.get(), "Select CP files", FileDialog.LOAD);
        fd.setDirectory(applicationModel.getDefaultDirectory());
        fd.setMultipleMode(true);
        fd.setFilenameFilter(((dir, name) -> allowedExtensions.stream().anyMatch(name::endsWith)));
        fd.setVisible(true);
        return fd.getFiles();
    }

    private void chooseExportExtension() {
        JDialog dialog = new JDialog(frameProvider.get(), "Converting options", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());

        JList<String> optionsList = new JList<>(options);
        optionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(optionsList);
        dialog.add(scrollPane);

        JButton selectButton = new JButton("Convert");
        selectButton.addActionListener(e -> {
            selectedOption = optionsList.getSelectedValue();

            if (selectedOption != null) { dialog.dispose(); }
            else {
                JOptionPane.showMessageDialog(dialog,
                        "Please select an option", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(selectButton, BorderLayout.SOUTH);

        dialog.setMinimumSize(new Dimension(300, 200));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void processSelectedFiles(File[] selectedFiles){
        for(File file : selectedFiles) {
            if(file.isFile()) { processFile(file); }
            if(file.isDirectory()) {
                File[] subFiles = file.listFiles();
                if(subFiles == null) continue;
                if(subFiles.length == 0) continue;

                // Skipping subdirectories for now
                for(File subFile : subFiles) {
                    if(subFile.isDirectory()) continue;
                    processFile(subFile);
                }
            }
        }
    }

    private void processFile(File file){
        try {
            if(file == null) return;
            Save save = importFile(file);
            if (save == null) return;

            exportFile(save, file);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Save importFile(File file) throws IOException {
        String fileName = file.getName();
        if(fileName.endsWith(".cp")){
            return new CpImporter().doImport(file);
        } else if(fileName.endsWith(".ori")) {
            return new OriImporter(frameProvider).doImport(file);
        } else if (fileName.endsWith(".orh")) {
            return new OrhImporter().doImport(file);
        } else if (fileName.endsWith(".fold")) {
            return new FoldImporter().doImport(file);
        }
        return null;
    }

    private void exportFile(Save save, File file) throws IOException {
        String exportFile = getFilePathNoExtension(file);

        if (selectedOption.endsWith(".cp)")){
            exportFile = exportFile.concat(".cp");
            new CpExporter(frameProvider).doExport(save, new File(exportFile));
        } else if (selectedOption.endsWith(".ori)")) {
            exportFile = exportFile.concat(".ori");
            new OriExporter().doExport(save, new File(exportFile));
        } else if (selectedOption.endsWith(".orh)")) {
            exportFile = exportFile.concat(".orh");
            new OrhExporter().doExport(save, new File(exportFile));
        } else if (selectedOption.endsWith(".fold)")) {
            exportFile = exportFile.concat(".fold");
            new FoldExporter().doExport(save, new File(exportFile));
        }
    }

    private String getFilePathNoExtension(File file){
        String filePath = file.getAbsolutePath();

        return allowedExtensions.stream().filter(filePath::endsWith).findFirst()
                .map(extension -> filePath.substring(0, filePath.length() - extension.length()))
                .orElse(filePath); // This should never happen
    }
}
