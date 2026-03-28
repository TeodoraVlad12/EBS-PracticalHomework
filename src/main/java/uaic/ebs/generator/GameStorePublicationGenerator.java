package uaic.ebs.generator;

import uaic.ebs.model.Company;
import uaic.ebs.model.GameStorePublication;
import uaic.ebs.model.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameStorePublicationGenerator {

    private final GeneratorConfig config;
    private final Random random;

    private static final Company[] COMPANIES = Company.values();
    private static final Genre[] GENRES = Genre.values();

    public GameStorePublicationGenerator(GeneratorConfig config) {
        this.config = config;
        this.random = new Random();
    }

    public GameStorePublicationGenerator(GeneratorConfig config, long seed) {
        this.config = config;
        this.random = new Random(seed);
    }

    public List<GameStorePublication> generate(int count) {
        List<GameStorePublication> publications = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            publications.add(generateOne());
        }
        return publications;
    }

    private GameStorePublication generateOne() {
        Company company = COMPANIES[random.nextInt(COMPANIES.length)];
        double price = config.getMinPrice() + random.nextDouble() * (config.getMaxPrice() - config.getMinPrice());
        int discount = config.getMinDiscount() + random.nextInt(config.getMaxDiscount() - config.getMinDiscount() + 1);
        double rating = config.getMinRating() + random.nextDouble() * (config.getMaxRating() - config.getMinRating());
        Genre genre = GENRES[random.nextInt(GENRES.length)];
        return new GameStorePublication(company, price, discount, rating, genre);
    }
}
