package uaic.ebs.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Getter
@Builder(toBuilder = true)
@JsonDeserialize(builder = GeneratorConfig.GeneratorConfigBuilder.class)
public class GeneratorConfig {

	private final int totalPublications;
	private final int totalSubscriptions;
	private final List<Integer> threadCounts;
	private final Map<String, Double> fieldFrequencies;
	private final Map<String, Double> equalityFrequencies;
	private final double minPrice;
	private final double maxPrice;
	private final int minDiscount;
	private final int maxDiscount;
	private final double minRating;
	private final double maxRating;

	@JsonPOJOBuilder(withPrefix = "")
	public static class GeneratorConfigBuilder {
	}

	public static GeneratorConfig fromFile(String filePath) throws IOException {
		return new ObjectMapper().readValue(new File(filePath), GeneratorConfig.class);
	}

	public static GeneratorConfig defaultConfig() {
		return GeneratorConfig.builder()
				.totalPublications(10_000)
				.totalSubscriptions(10_000)
				.threadCounts(List.of(1, 2, 4))
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
