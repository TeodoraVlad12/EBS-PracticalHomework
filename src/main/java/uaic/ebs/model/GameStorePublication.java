package uaic.ebs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameStorePublication {
    private final Company company;
    private final double price;
    private final int discount;
    private final double rating;
    private final Genre genre;

    @Override
    public String toString() {
        return String.format("{(company,\"%s\");(price,%.2f);(discount,%d);(rating,%.2f);(genre,\"%s\")}",
                company.name(), price, discount, rating, genre.name());
    }
}
