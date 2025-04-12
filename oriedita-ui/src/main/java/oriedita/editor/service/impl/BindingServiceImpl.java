package oriedita.editor.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import org.tinylog.Logger;
import oriedita.common.converter.DoubleConverter;
import oriedita.common.converter.IntConverter;
import oriedita.editor.AbstractModel;
import oriedita.editor.Colors;
import oriedita.common.converter.Converter;
import oriedita.editor.service.BindingService;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class BindingServiceImpl implements BindingService {
    @Override
    public <T> void addBinding(AbstractModel model, JTextField component, String property, Converter<T, String> converter) {

        try {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(property, model.getClass());

            if (converter == null) {
                //noinspection unchecked
                converter = (Converter<T, String>) findConverter(propertyDescriptor.getPropertyType()).orElseThrow(
                        () -> new RuntimeException("Could not find Converter from " + propertyDescriptor.getPropertyType() + " to String")
                );
            }
            var finalConverter = converter;


            //noinspection unchecked
            component.setText(finalConverter.convert((T) propertyDescriptor.getReadMethod().invoke(model)));

            AtomicReference<String> value = new AtomicReference<>(component.getText());

            component.addCaretListener(e -> {
                try {
                    if (!value.get().equals(component.getText())) {
                        value.set(component.getText());
                        if (finalConverter.canConvertBack(component.getText())){
                            propertyDescriptor.getWriteMethod().invoke(model, finalConverter.convertBack(component.getText()));
                        }
                        component.setBackground(finalConverter.canConvertBack(component.getText())
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

    @Override
    public <T> void addBinding(AbstractModel model, JComboBox<T> component, String property) {
        try {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(property, model.getClass());
            component.setSelectedItem(propertyDescriptor.getReadMethod().invoke(model));
            model.addPropertyChangeListener(property, e -> {
                if (e.getNewValue() != component.getSelectedItem()) {
                    component.setSelectedItem(e.getNewValue());
                }
            });
            component.addActionListener(e -> {
                try {
                    propertyDescriptor.getWriteMethod().invoke(model, component.getSelectedItem());
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Optional<Converter<T, String>> findConverter(Class<?> propertyType) {
        return switch (propertyType.getName()) {
            case "double", "java.lang.Double" -> //noinspection unchecked
                    Optional.of((Converter<T, String>) new DoubleConverter());
            case "int", "java.lang.Integer" -> //noinspection unchecked
                    Optional.of((Converter<T, String>) new IntConverter());
            default -> Optional.empty();
        };
    }
}
