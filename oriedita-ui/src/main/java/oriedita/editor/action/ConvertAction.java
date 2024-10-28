package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import oriedita.editor.FrameProvider;
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
import java.util.Objects;

@ApplicationScoped
@ActionHandler(ActionType.convertAction)
public class ConvertAction extends AbstractOrieditaAction{
    @Inject
    FrameProvider frameProvider;
    private String selectedOption;
    List<String> allowedExtensions = Arrays.asList(".cp", ".ori", ".orh", ".fold");
    String[] options = {"Crease Pattern (.cp)", "Ori (.ori)", "Orihime (.orh)", "FOLD (.fold)"};

    @Inject
    public ConvertAction(){}

    @Override
    public void actionPerformed(ActionEvent e) {
        File file = selectFile();
        if(file == null) return;

        try {
            Save save = importFile(Objects.requireNonNull(file));
            if (save == null) return;

            openDialog();
            exportFile(save, file);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private File selectFile(){
        FileDialog fileDialog = new FileDialog(frameProvider.get(), "Select a file", FileDialog.LOAD);
        fileDialog.setFilenameFilter((dir, name) -> allowedExtensions.stream().anyMatch(name::endsWith));
        fileDialog.setVisible(true);

        if (fileDialog.getFile() == null) {
            System.out.println("File selection cancelled.");
            return null;
        }

        return new File(fileDialog.getDirectory(), fileDialog.getFile());
    }

    private String getFilePathNoExtension(File file){
        String filePath = file.getAbsolutePath();

        if(filePath.endsWith(".cp")){
            filePath = filePath.substring(0, filePath.length() - ".cp".length());
        } else if(filePath.endsWith(".ori")){
            filePath = filePath.substring(0, filePath.length() - ".ori".length());
        } else if(filePath.endsWith(".orh")){
            filePath = filePath.substring(0, filePath.length() - ".orh".length());
        } else if(filePath.endsWith(".fold")){
            filePath = filePath.substring(0, filePath.length() - ".fold".length());
        }

        return filePath;
    }

    private void openDialog() {
        JDialog dialog = new JDialog(frameProvider.get(), "Converting options", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());

        JList<String> optionsList = new JList<>(options);
        optionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(optionsList);
        dialog.add(scrollPane);

        JButton selectButton = new JButton("Convert");
        selectButton.addActionListener(e -> {
            selectedOption = optionsList.getSelectedValue();

            if (selectedOption != null) {
                dialog.dispose();
            } else {
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
}
