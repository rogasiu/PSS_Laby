package pl.pss.PSS.sercurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // żądania wymagające logowania
                .antMatchers("/").hasAnyAuthority("ROLE_USER")
                .antMatchers("/userDetails").hasAnyAuthority("ROLE_USER")
                .antMatchers("/editUser").hasAnyAuthority("ROLE_USER")
                .antMatchers("/delegationList").hasAnyAuthority("ROLE_USER")
                .antMatchers("/changePassword").hasAnyAuthority("ROLE_USER")
                .antMatchers("/addDelegation").hasAnyAuthority("ROLE_USER")
                .antMatchers("/deleteDelegation/*").hasAnyAuthority("ROLE_USER")
                .antMatchers("/editDelegation/*").hasAnyAuthority("ROLE_USER")
                .anyRequest().permitAll()
                .and().csrf().disable()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .loginProcessingUrl("/login_process")
                .failureUrl("/login?error=true")
                .defaultSuccessUrl("/")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login");
    }

    @Autowired
    DataSource dataSource;
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.jdbcAuthentication()
                .usersByUsernameQuery("SELECT u.email,u.password,u.status FROM users u WHERE u.email=?")
                .authoritiesByUsernameQuery(
                        "SELECT u.email, r.role_name FROM users u JOIN users_to_roles ur ON ur.user_id = u.user_id" +
                                " JOIN roles r ON r.role_id = ur.role_id WHERE u.email=?"
                )
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }
}
