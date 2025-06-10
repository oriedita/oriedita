package oriedita.editor.databinding;

import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;
import oriedita.editor.canvas.LineStyle;
import origami.crease_pattern.CustomLineTypes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

// Tests that no applicationModel
class ApplicationModelTest {
    private static final Map<Class<?>, Object[]> testValues = new HashMap<>();
    private static final Map<String, Object[]> specialProperties = new HashMap<>();

    private ApplicationModel applicationModel;
    private BeanInfo beanInfo;

    @BeforeEach
    void setUp() throws IntrospectionException {
        applicationModel = new ApplicationModel();
        beanInfo = Introspector.getBeanInfo(ApplicationModel.class, Object.class);
    }

    @BeforeAll
    static void beforeAll() {
        // testValues needs to contain arrays with 2 different values for each type that is used in the model.
        testValues.put(double.class, new Object[]{0.5, 2.5});
        testValues.put(int.class, new Object[]{1, 2});
        testValues.put(long.class, new Object[]{5L, 10L});
        testValues.put(boolean.class, new Object[]{true, false});
        testValues.put(String.class, new String[]{"abc", "123"});
        testValues.put(Color.class, new Color[]{Color.MAGENTA, Color.YELLOW});
        testValues.put(CustomLineTypes.class, new CustomLineTypes[]{CustomLineTypes.EDGE, CustomLineTypes.MOUNTAIN});
        testValues.put(LineStyle.class, new LineStyle[]{LineStyle.BLACK_ONE_DOT, LineStyle.COLOR});
        testValues.put(Point.class, new Point[]{new Point(0, 0), new Point(5, 10)});
        testValues.put(Dimension.class, new Dimension[]{new Dimension(1, 2), new Dimension(5, 10)});

        // specify properties that can not be tested with the standard values, eg. because of validations or generic types
        specialProperties.put("numPolygonCorners", new Object[]{5, 6}); // numPolygonCorners ignores values below 3, so higher test values are needed
        specialProperties.put("recentFileList", new Object[]{
                List.of(new File("")),
                List.of(new File(""), new File("")),
        });
    }

    /**
     * Checks that all properties of the applicationModel correctly trigger PropertyChangeEvents when set.
     * This includes the propertyName set in the event, as well as the old and new value. It also checks that
     * Methods with the proper names (get{Property} and set{Property}) exist.
     */
    @Test
    void testSetTriggersPropertyChangeEvent() throws InvocationTargetException, IllegalAccessException {
        for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
            Object[] testValues = getTestValues(property);
            Object value1 = testValues[0];
            Object value2 = testValues[1];

            assertNotNull(property.getReadMethod(), "Property should have read method. Property: " + property);
            assertNotNull(property.getWriteMethod(), "Property should have write method. Property: " + property);

            property.getWriteMethod().invoke(applicationModel, value1);

            AtomicBoolean changeTriggered = new AtomicBoolean(false);
            PropertyChangeListener listener = evt -> {
                assertFalse(changeTriggered.get(), "PropertyChangeListener should only be triggered once. Property: " + property);

                changeTriggered.set(true);

                assertEquals(property.getName(), evt.getPropertyName(),
                        "Property name in PropertyChangeEvent should match actual property name. Property: " + property);
                assertEquals(value1, evt.getOldValue(), "PropertyChangeEvent should have the correct old value. Property: " + property);
                assertEquals(value2, evt.getNewValue(), "PropertyChangeEvent should have the correct new value. Property: " + property);
            };
            applicationModel.addPropertyChangeListener(listener);
            property.getWriteMethod().invoke(applicationModel, value2);
            applicationModel.removePropertyChangeListener(listener);

            assertEquals(value2, property.getReadMethod().invoke(applicationModel),
                    "Property should have new value after calling setter. Property: " + property);
            assertTrue(changeTriggered.get(), "Setting a property should trigger a PropertyChangedEvent. Property: " + property);
        }
    }

    /**
     * Checks that reset() actually resets all properties in the model
     */
    @Test
    void testReset() throws InvocationTargetException, IllegalAccessException {
        applicationModel.reset();
        for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
            Object[] testValues = getTestValues(property);
            Object resetValue = property.getReadMethod().invoke(applicationModel);
            Object testVal = testValues[0].equals(resetValue)? testValues[1] : testValues[0];

            property.getWriteMethod().invoke(applicationModel, testVal);
            applicationModel.reset();
            assertEquals(resetValue, property.getReadMethod().invoke(applicationModel),
                    "Property should be assigned a value in reset(). Property: " + property);
        }
    }

    /**
     * checks that the values which are set in restorePrefDefaults() do not differ from the values set in reset()
     */
    @Test
    void testRestorePrefDefault() throws InvocationTargetException, IllegalAccessException {
        for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
            applicationModel.reset();
            Object resetValue = property.getReadMethod().invoke(applicationModel);
            applicationModel.restorePrefDefaults();
            Object prefDefaultValue = property.getReadMethod().invoke(applicationModel);

            assertEquals(resetValue, prefDefaultValue,
                    "value set in restorePrefDefault() should be the same as in reset(). Property: " + property);
        }
    }

    /**
     * checks that the all properties of the model are copied inside set(). Some properties may be ignored due to not
     * being able to reliably check equality, eg. Lists. Those will have to be manually checked.
     */
    @Test
    void testSet() throws InvocationTargetException, IllegalAccessException {
        ApplicationModel secondModel = new ApplicationModel();
        for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
            secondModel.reset();
            applicationModel.reset();
            Object[] testValues = getTestValues(property);
            Object resetValue = property.getReadMethod().invoke(applicationModel);
            Object testVal = testValues[0].equals(resetValue)? testValues[1] : testValues[0];
            property.getWriteMethod().invoke(applicationModel, testVal);
            secondModel.set(applicationModel);
            if (property.getPropertyType().equals(List.class)){
                // cannot check equality of lists because Lists are compared by reference, which does not work when value is copied
                Logger.info("Ignored Property " + property
                        + " in testSet because equality could not be checked. \n Please manually verify it is correctly copied in set()");
            } else {
                assertEquals(testVal, property.getReadMethod().invoke(secondModel),
                        "Property should be copied in set(). Property: " + property);
            }
        }
    }

    @Nonnull
    private Object[] getTestValues(PropertyDescriptor property) {
        Class<?> cls = property.getPropertyType();
        if (specialProperties.containsKey(property.getName())) {
            return specialProperties.get(property.getName());
        } else if (testValues.containsKey(cls)){
            return testValues.get(cls);
        }
        fail("could not check property " + property.getName() + ", because type "
                + cls.getSimpleName() + " has no test values");
        return new Object[0]; // unreachable
    }
}