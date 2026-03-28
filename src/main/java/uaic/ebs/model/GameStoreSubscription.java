package uaic.ebs.model;

import lombok.Getter;

import java.util.List;
import java.util.StringJoiner;

@Getter
public class GameStoreSubscription {
    private final List<Constraint> constraints;

    public GameStoreSubscription(List<Constraint> constraints) {
        this.constraints = List.copyOf(constraints);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(";", "{", "}");
        for (Constraint c : constraints) {
            joiner.add(c.toString());
        }
        return joiner.toString();
    }
}
