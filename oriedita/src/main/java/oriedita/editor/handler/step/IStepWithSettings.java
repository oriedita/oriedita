package oriedita.editor.handler.step;

import oriedita.editor.handler.MouseHandlerSettingGroup;

import java.util.Collection;

public interface IStepWithSettings {
    Collection<MouseHandlerSettingGroup> getSettings();
}
