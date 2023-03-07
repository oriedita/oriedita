package oriedita.editor.action;

import jakarta.enterprise.context.ApplicationScoped;

import java.awt.event.ActionEvent;

@ApplicationScoped
@ActionHandler(ActionType.creasePatternZoomInAction)
public class CreasePatternZoomInAction extends AbstractCreasePatternZoomAction{
    @Override
    public void actionPerformed(ActionEvent e) {
        zoom(-1);
    }
}
