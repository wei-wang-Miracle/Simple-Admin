package com.simple.core.security;

import com.simple.core.filter.BaseLogFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.DigestUtils;

/**
 * JWT Spring Security 配置
 * 适配 SpringBoot 2.7.12 + Spring Security 5.x
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class JwtSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private final IgnoredUrlsProperties ignoredUrlsProperties;
    private final EntryPointUnauthorizedHandler entryPointUnauthorizedHandler;
    private final RestAccessDeniedHandler restAccessDeniedHandler;
    private final UserDetailsService userDetailsService;
    private final BaseLogFilter baseLogFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF，因为使用 JWT
                .csrf().disable()
                // 禁用 session，因为使用 JWT
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 配置请求授权
                .authorizeRequests()
                // 放行忽略的 URL
                .antMatchers(ignoredUrlsProperties.getAdminAuthUrls().toArray(new String[0])).permitAll()
                // /admin/** 需要认证
                .antMatchers("/admin/**").authenticated()
                // 其他请求放行
                .anyRequest().permitAll()
                .and()
                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // 添加日志记录过滤器（在JWT过滤器之后，确保SecurityContext已设置）
                .addFilterAfter(baseLogFilter, JwtAuthenticationTokenFilter.class)
                // 异常处理
                .exceptionHandling()
                .authenticationEntryPoint(entryPointUnauthorizedHandler)
                .accessDeniedHandler(restAccessDeniedHandler)
                .and()
                // 禁用表单登录
                .formLogin().disable()
                // 禁用 HTTP Basic
                .httpBasic().disable()
                // 启用 CORS
                .cors();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 密码编码器（使用 MD5）
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals(encode(rawPassword));
            }
        };
    }
}
