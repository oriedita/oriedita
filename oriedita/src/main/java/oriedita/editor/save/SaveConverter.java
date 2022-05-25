package oriedita.editor.save;

public class SaveConverter {
    public static SaveV1_1 saveToV1_1(Save save) {
        SaveV1_1 newSave = new SaveV1_1();
        newSave.set(save);
        newSave.setApplicationModel(save.getApplicationModel());
        newSave.setCanvasModel(save.getCanvasModel());
        newSave.setCreasePatternCamera(save.getCreasePatternCamera());
        newSave.setFoldedFigureModel(save.getFoldedFigureModel());
        newSave.setGridModel(save.getGridModel());
        newSave.setPoints(save.getPoints());
        newSave.setTexts(save.getTexts());
        return newSave;
    }

    public static Save convertToNewestSave(Save save) {
        return saveToV1_1(save);
    }
}
