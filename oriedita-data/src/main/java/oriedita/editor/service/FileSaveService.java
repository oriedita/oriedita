package oriedita.editor.service;

import oriedita.editor.FrameProvider;
import oriedita.editor.exception.FileReadingException;
import oriedita.editor.save.Save;

import javax.swing.JPanel;
import java.io.File;

public interface FileSaveService {
    void openFile(File file) throws FileReadingException;

    void openFile();

    void importPref (JPanel parent, FrameProvider frameProvider, ButtonService buttonService);

    void exportPref ();

    void importFile();

    void exportFile();

    File selectSaveFile();

    File selectImportFile();

    File selectExportFile();

    Save readImportFile(File file) throws FileReadingException;

    Save readImportFile(File file, boolean askOnUnknownFormat) throws FileReadingException;

    void saveFile();

    void saveAsFile();

    boolean readBackgroundImageFromFile();

    void initAutoSave();
}
