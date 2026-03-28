package uaic.ebs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Constraint {
    private final String fieldName;
    private final Operator operator;
    private final Object value;

    @Override
    public String toString() {
        String formattedValue = (value instanceof String)
                ? "\"" + value + "\""
                : value.toString();
        return "(" + fieldName + "," + operator.getSymbol() + "," + formattedValue + ")";
    }
}
