package uaic.ebs.generator;

import uaic.ebs.model.*;

import java.util.*;

public class GameStoreSubscriptionGenerator {

    private static final String FIELD_COMPANY = "company";
    private static final String FIELD_PRICE = "price";
    private static final String FIELD_DISCOUNT = "discount";
    private static final String FIELD_RATING = "rating";
    private static final String FIELD_GENRE = "genre";

    private static final List<String> ALL_FIELDS = List.of(
            FIELD_COMPANY, FIELD_PRICE, FIELD_DISCOUNT, FIELD_RATING, FIELD_GENRE);

    private static final Operator[] NUMERIC_OPS = Operator.values();
    private static final Operator[] STRING_OPS = { Operator.EQUAL, Operator.NOT_EQUAL };

    private static final Company[] COMPANIES = Company.values();
    private static final Genre[] GENRES = Genre.values();

    private final GeneratorConfig config;
    private final Random random;

    public GameStoreSubscriptionGenerator(GeneratorConfig config) {
        this.config = config;
        this.random = new Random();
    }

    public GameStoreSubscriptionGenerator(GeneratorConfig config, long seed) {
        this.config = config;
        this.random = new Random(seed);
    }

    public List<GameStoreSubscription> generate(int count) {
        boolean[][] fieldPresence = computeFieldPresence(count);
        Operator[][] fieldOperators = computeFieldOperators(count, fieldPresence);

        List<GameStoreSubscription> subscriptions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            List<Constraint> constraints = new ArrayList<>();
            for (int f = 0; f < ALL_FIELDS.size(); f++) {
                if (fieldPresence[f][i]) {
                    constraints.add(buildConstraint(ALL_FIELDS.get(f), fieldOperators[f][i]));
                }
            }
            subscriptions.add(new GameStoreSubscription(constraints));
        }
        return subscriptions;
    }

    private boolean[][] computeFieldPresence(int count) {
        boolean[][] presence = new boolean[ALL_FIELDS.size()][count];
        for (int f = 0; f < ALL_FIELDS.size(); f++) {
            String field = ALL_FIELDS.get(f);
            double freq = config.getFieldFrequencies().getOrDefault(field, 0.0);
            int required = (int) Math.ceil(freq * count);
            required = Math.min(required, count);

            List<Integer> indices = new ArrayList<>(count);
            for (int i = 0; i < count; i++)
                indices.add(i);
            Collections.shuffle(indices, random);

            for (int j = 0; j < required; j++) {
                presence[f][indices.get(j)] = true;
            }
        }
        return presence;
    }

    private Operator[][] computeFieldOperators(int count, boolean[][] fieldPresence) {
        Operator[][] operators = new Operator[ALL_FIELDS.size()][count];
        for (int f = 0; f < ALL_FIELDS.size(); f++) {
            String field = ALL_FIELDS.get(f);
            boolean isString = field.equals(FIELD_COMPANY) || field.equals(FIELD_GENRE);

            double eqFreq = config.getEqualityFrequencies().getOrDefault(field, -1.0);

            List<Integer> presentIndices = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                if (fieldPresence[f][i])
                    presentIndices.add(i);
            }

            if (eqFreq >= 0) {
                int equalCount = (int) Math.ceil(eqFreq * presentIndices.size());
                equalCount = Math.min(equalCount, presentIndices.size());

                List<Integer> shuffled = new ArrayList<>(presentIndices);
                Collections.shuffle(shuffled, random);

                for (int j = 0; j < shuffled.size(); j++) {
                    int idx = shuffled.get(j);
                    if (j < equalCount) {
                        operators[f][idx] = Operator.EQUAL;
                    } else {
                        operators[f][idx] = pickNonEqualOperator(isString);
                    }
                }
            } else {
                for (int idx : presentIndices) {
                    operators[f][idx] = isString
                            ? STRING_OPS[random.nextInt(STRING_OPS.length)]
                            : NUMERIC_OPS[random.nextInt(NUMERIC_OPS.length)];
                }
            }
        }
        return operators;
    }

    private Operator pickNonEqualOperator(boolean isString) {
        if (isString) {
            return Operator.NOT_EQUAL;
        }
        Operator[] nonEqual = { Operator.NOT_EQUAL, Operator.LESS, Operator.LESS_OR_EQUAL,
                Operator.GREATER, Operator.GREATER_OR_EQUAL };
        return nonEqual[random.nextInt(nonEqual.length)];
    }

    private Constraint buildConstraint(String field, Operator op) {
        return switch (field) {
            case FIELD_COMPANY -> new Constraint(field, op, COMPANIES[random.nextInt(COMPANIES.length)].name());
            case FIELD_GENRE -> new Constraint(field, op, GENRES[random.nextInt(GENRES.length)].name());
            case FIELD_PRICE -> new Constraint(field, op,
                    Math.round(
                            (config.getMinPrice() + random.nextDouble() * (config.getMaxPrice() - config.getMinPrice()))
                                    * 100.0)
                            / 100.0);
            case FIELD_DISCOUNT -> new Constraint(field, op,
                    config.getMinDiscount() + random.nextInt(config.getMaxDiscount() - config.getMinDiscount() + 1));
            case FIELD_RATING -> new Constraint(field, op,
                    Math.round((config.getMinRating()
                            + random.nextDouble() * (config.getMaxRating() - config.getMinRating())) * 100.0) / 100.0);
            default -> throw new IllegalArgumentException("Unknown field: " + field);
        };
    }
}
