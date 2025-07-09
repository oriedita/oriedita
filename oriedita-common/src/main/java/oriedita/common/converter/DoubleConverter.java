package oriedita.common.converter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;

public class DoubleConverter implements Converter<Double, String> {
    private final DecimalFormat format;

    public DoubleConverter() {
        this.format = null;
    }

    public DoubleConverter(String format) {
        this.format = new DecimalFormat(format, DecimalFormatSymbols.getInstance(Locale.US));
    }

    @Override
    public String convert(Double value) {
        if (format != null) {
            return format.format(value);
        }
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
