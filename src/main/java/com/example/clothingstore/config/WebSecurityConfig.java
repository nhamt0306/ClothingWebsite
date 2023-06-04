package com.example.clothingstore.config;

import com.example.clothingstore.security.OAuth2.CustomOAuth2UserService;
import com.example.clothingstore.security.OAuth2.HttpCookiesOAuth2AuthorizationRequestRepository;
import com.example.clothingstore.security.jwt.JwtEntryPoint;
import com.example.clothingstore.security.jwt.JwtTokenFilter;
import com.example.clothingstore.security.principal.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.clothingstore.model.RoleName.*;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailService userDetailServices;
    @Autowired
    JwtEntryPoint jwtEntryPoint;
    @Bean
    public JwtTokenFilter jwtTokenFilter(){
        return new JwtTokenFilter();
    }
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailServices)
                .passwordEncoder(passwordEncoder());
    }
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable().
                authorizeRequests().antMatchers(
                        "/failed",
                        "/oauth2/**",
                        "/login",
                        "/register",
                        "/category/**",
                        "/type/**",
                        "/product/**",
                        "/typeProduct/**",
                        "/order/**",
                        "/comment/product/**",
                        "/profile/**",
                        "/recoveryPassword/*",
                        "/comment",
                        "/questions",
                        "/questions/**"
                ).permitAll()
                .antMatchers().hasAnyRole(ADMIN.name(), USER.name()) //Các API cần đăng nhập bằng tk admin, user
                .antMatchers("/admin/**").hasAuthority("ADMIN") //Các API cần đăng nhập bằng tk admin
                .antMatchers("/user/**").hasAuthority(USER.name()) //Các API cần đăng nhập bằng tk user
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtEntryPoint)
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(new HttpCookiesOAuth2AuthorizationRequestRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(new CustomOAuth2UserService())
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticaionFailureHandler);
        httpSecurity.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(new CORSFilter(), ChannelProcessingFilter.class);
    }

    @Autowired
    com.example.clothingstore.security.OAuth2.oAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    com.example.clothingstore.security.OAuth2.oAuth2AuthenticaionFailureHandler oAuth2AuthenticaionFailureHandler;
//    @Autowired
//    private CustomOAuth2UserService oAuth2UserService;
//
//    @Autowired
//    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
}
