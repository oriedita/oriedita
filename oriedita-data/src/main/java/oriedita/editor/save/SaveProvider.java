package oriedita.editor.save;

public class SaveProvider {
    public static Save createInstance() {
        return new SaveV1_1();
    }
}
