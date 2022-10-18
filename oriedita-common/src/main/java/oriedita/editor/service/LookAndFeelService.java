package oriedita.editor.service;

public interface LookAndFeelService {
    void init();

    void updateButtonIcons();

    void toggleDarkMode();

    void registerLafModeListener(LookAndFeelListener listener);

    void registerFlatLafSource();

    interface LookAndFeelListener {
        void handleLookAndFeelChange(String lookAndFeel);
    }
}
