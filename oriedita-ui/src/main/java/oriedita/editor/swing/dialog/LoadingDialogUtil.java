package oriedita.editor.swing.dialog;

public class LoadingDialogUtil {
    private static LoadingDialog loadingDialog;

    public static void show(){
        loadingDialog = new LoadingDialog();
    }

    public static void hide() {
        loadingDialog.dispose();
    }
}
