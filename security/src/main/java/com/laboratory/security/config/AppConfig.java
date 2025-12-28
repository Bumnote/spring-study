package com.laboratory.security.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
public class AppConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public UserDetailsService inMemoryUserDetailsManager() {
    var inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
    /* [2] 임시 사용자 등록 */
    inMemoryUserDetailsManager.createUser(
        User.builder()
            .username("junhyunny")
            .password(passwordEncoder().encode("12345"))
            .build()
    );
    /* [3] UserDetailsService 객체 반환 */
    return inMemoryUserDetailsManager;
  }

  @Bean
  public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
    var jdbcTokenRepository = new JdbcTokenRepositoryImpl();
    /* [1] 데이터소스 객체 주입 */
    jdbcTokenRepository.setDataSource(dataSource);
    /* [2] 테이블 생성 옵션 활성화 */
    jdbcTokenRepository.setCreateTableOnStartup(true);
    return jdbcTokenRepository;
  }

}