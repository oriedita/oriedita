package oriedita.common.converter;

import java.util.regex.Pattern;

public class IntConverter implements Converter<Integer, String> {
    @Override
    public String convert(Integer value) {
        return value.toString();
    }

    @Override
    public Integer convertBack(String value) {
        return Integer.parseInt(value);
    }

    @Override
    public boolean canConvert(Integer value) {
        return true;
    }

    @Override
    public boolean canConvertBack(String value) {
        return Pattern.matches("^-?\\d+(E-?\\d+)?$", value);
    }
}
