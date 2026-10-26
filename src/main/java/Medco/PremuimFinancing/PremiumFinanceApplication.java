package Medco.PremuimFinancing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@ComponentScan(
        basePackages = "Medco.PremuimFinancing",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "Medco\\.PremuimFinancing\\.Controller\\.(Car|Insurance|Policy|Quotation|Payment)\\..*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "Medco\\.PremuimFinancing\\.ServiceImpl\\.User\\.UsersLoader\\..*")
        }
)
public class PremiumFinanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PremiumFinanceApplication.class, args);
	}

}
