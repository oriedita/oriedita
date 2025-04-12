package oriedita.common.converter;

import java.util.regex.Pattern;

public class DoubleConverter implements Converter<Double, String> {
    @Override
    public String convert(Double value) {
        return value.toString();
    }

    @Override
    public Double convertBack(String value) {
        return Double.parseDouble(value);
    }

    @Override
    public boolean canConvert(Double value) {
        return true;
    }

    @Override
    public boolean canConvertBack(String value) {
        return Pattern.matches("^-?\\d+(\\.\\d+)?(E-?\\d+)?$", value);
    }
}
