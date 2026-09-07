package com.medsync.api_gateway;

import com.medsync.common.config.CommonSecurityConfig;
import com.medsync.common.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;

@SpringBootApplication(
		exclude = {
				SecurityAutoConfiguration.class,
				UserDetailsServiceAutoConfiguration.class,
				DataSourceAutoConfiguration.class,
				DataSourceTransactionManagerAutoConfiguration.class,
				HibernateJpaAutoConfiguration.class,
				ManagementWebSecurityAutoConfiguration.class
		}
)
@ComponentScan(
		basePackages = {"com.medsync.api_gateway", "com.medsync.common"},
		excludeFilters = @ComponentScan.Filter(
				type = FilterType.ASSIGNABLE_TYPE,
				classes = CommonSecurityConfig.class
		)
)
@EnableConfigurationProperties(JwtProperties.class)
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
}