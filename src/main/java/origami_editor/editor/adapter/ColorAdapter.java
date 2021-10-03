package origami_editor.editor.adapter;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.awt.*;

public class ColorAdapter extends XmlAdapter<AdaptedColor, java.awt.Color> {
    @Override
    public Color unmarshal(AdaptedColor adaptedColor) throws Exception {
        return new Color(adaptedColor.getValue());
    }

    @Override
    public AdaptedColor marshal(Color color) throws Exception {
        AdaptedColor adaptedColor = new AdaptedColor();
        adaptedColor.setValue(color.getRGB());
        return adaptedColor;
    }
}
