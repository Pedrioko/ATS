package com.gitlab.pedrioko.spring.security;

import com.gitlab.pedrioko.core.api.StaticResouceLocation;
import com.gitlab.pedrioko.core.view.util.ApplicationContextUtils;
import com.gitlab.pedrioko.domain.enumdomain.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Class WebSecurityConfig.
 */
@Configuration
@Order(200)
@EnableJpaAuditing
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService customUserDetailsService;
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    /**
     * The auth provider.
     */
    //   @Autowired
    // private JdbcAuthenticationProvider authProvider;

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.config.annotation.web.configuration.
     * WebSecurityConfigurerAdapter#configure(org.springframework.security.
     * config.annotation.authentication.builders.AuthenticationManagerBuilder)
     */
    @Override
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //   auth.authenticationProvider(authProvider);
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.config.annotation.web.configuration.
     * WebSecurityConfigurerAdapter#configure(org.springframework.security.
     * config.annotation.web.builders.HttpSecurity)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        List<StaticResouceLocation> beans = ApplicationContextUtils.getBeans(StaticResouceLocation.class);
        String[] strings = beans.stream().map(e -> e.getPath()).collect(Collectors.toSet()).toArray(new String[]{});
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry and = http.authorizeRequests().antMatchers("/ws/**").permitAll().and().authorizeRequests()
                .antMatchers("/test/**", "/file/**", "/tester/**", "/videos", "/css/**", "/signaling", "/components/**", "/bootstrap/**", "/fonts/**", "/js/**",
                        "/images/**", "/zkau/**", "/login", "/recovery", "/register")
                .permitAll().and().authorizeRequests();
        for (String e : Arrays.asList(strings)) {
            and = and.antMatchers(e).permitAll();
        }
        and.antMatchers("/**")
                .hasAnyAuthority(TipoUsuario.ROLE_ADMIN.name(), TipoUsuario.ALL.name(),
                        TipoUsuario.ROLE_USER.name())
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler)
                .failureUrl("/login?error=true")
                .and().logout().deleteCookies("JSESSIONID").and().csrf().disable();
    }


    /**
     * Password encoder.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}