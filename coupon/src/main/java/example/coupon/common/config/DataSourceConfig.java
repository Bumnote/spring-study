package example.coupon.common.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource.business")
  public DataSourceProperties businessDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource.business.hikari")
  public HikariDataSource businessDataSource(
      @Qualifier("businessDataSourceProperties") DataSourceProperties properties) {
    return properties.initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.lock")
  public DataSourceProperties lockDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @ConfigurationProperties("spring.datasource.lock.hikari")
  public HikariDataSource lockDataSource(
      @Qualifier("lockDataSourceProperties") DataSourceProperties properties) {
    return properties.initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }
}
