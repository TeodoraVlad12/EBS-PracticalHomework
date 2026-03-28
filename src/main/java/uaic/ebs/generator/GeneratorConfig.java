package uaic.ebs.generator;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder(toBuilder = true)
public class GeneratorConfig {

	private final int totalPublications;
	private final int totalSubscriptions;
	private final int threadCount;
	private final Map<String, Double> fieldFrequencies;
	private final Map<String, Double> equalityFrequencies;
	private final double minPrice;
	private final double maxPrice;
	private final int minDiscount;
	private final int maxDiscount;
	private final double minRating;
	private final double maxRating;

	public static GeneratorConfig defaultConfig() {
		return GeneratorConfig.builder()
				.totalPublications(10_000)
				.totalSubscriptions(10_000)
				.threadCount(4)
				.fieldFrequencies(Map.of(
						"company", 0.90,
						"price", 0.80,
						"discount", 0.60,
						"rating", 0.70,
						"genre", 0.40))
				.equalityFrequencies(Map.of(
						"company", 0.70))
				.minPrice(0.0)
				.maxPrice(70.0)
				.minDiscount(0)
				.maxDiscount(100)
				.minRating(0.0)
				.maxRating(10.0)
				.build();
	}
}
