package se.magnus.microservices.composite.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.reactive.function.client.WebClient;
import se.magnus.microservices.composite.product.services.ProductCompositeIntegration;

import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootApplication
@ComponentScan("se.magnus")
public class ProductCompositeServiceApplication {
	@Autowired
	public ProductCompositeServiceApplication(@Lazy ProductCompositeIntegration integration) {
		this.integration = integration;
	}
	private final ProductCompositeIntegration integration;

	@Bean
	ReactiveHealthContributor coreServices() {
		final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();
		registry.put("product", integration::getProductHealth);
		registry.put("recommendation", integration::getRecommendationHealth);
		registry.put("review", integration::getReviewHealth);
		return CompositeReactiveHealthContributor.fromMap(registry);
	}

	@Bean
	@LoadBalanced
	WebClient.Builder builder() {
		return WebClient.builder();
	}

	public static void main(String[] args) {
		SpringApplication.run(ProductCompositeServiceApplication.class, args);
	}
}
