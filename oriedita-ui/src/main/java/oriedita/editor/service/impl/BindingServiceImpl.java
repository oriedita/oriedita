package oriedita.editor.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.tinylog.Logger;
import oriedita.editor.AbstractModel;
import oriedita.editor.Colors;
import oriedita.common.converter.Converter;
import oriedita.editor.service.BindingService;

import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class BindingServiceImpl implements BindingService {
    @Override
    public <T> void addBinding(AbstractModel model, JTextField component, String property, Converter<T, String> converter) {
        try {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(property, model.getClass());
            //noinspection unchecked
            component.setText(converter.convert((T) propertyDescriptor.getReadMethod().invoke(model)));

            AtomicReference<String> value = new AtomicReference<>(component.getText());

            component.addCaretListener(e -> {
                try {
                    if (!value.get().equals(component.getText())) {
                        value.set(component.getText());
                        if (converter.canConvertBack(component.getText())){
                            propertyDescriptor.getWriteMethod().invoke(model, converter.convertBack(component.getText()));
                        }
                        component.setBackground(converter.canConvertBack(component.getText())
                                ? UIManager.getColor("TextField.background") :
                                Colors.get(Colors.INVALID_INPUT));
                    }
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    Logger.error(ex);
                }
            });
            model.addPropertyChangeListener(property, e -> {
                if (!e.getNewValue().toString().equals(component.getText())) {
                    if (!component.isFocusOwner()){
                        component.setText(e.getNewValue().toString());
                    }
                }
            });
            component.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    try {
                        component.setText(propertyDescriptor.getReadMethod().invoke(model).toString());
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        Logger.error(ex);
                    }
                }
            });

        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
