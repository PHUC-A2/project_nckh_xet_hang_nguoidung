package com.example.xephangnguoidung.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.example.xephangnguoidung.application.service.CustomUserDetailsService;
import com.example.xephangnguoidung.application.service.NguoiDungService;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

        // Bean để mã hóa mật khẩu sử dụng BCrypt
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // Bean để cung cấp dịch vụ UserDetailsService tùy chỉnh
        @Bean
        public UserDetailsService userDetailsService(NguoiDungService nguoiDungService) {
                return new CustomUserDetailsService(nguoiDungService);
        }

        // Bean để cấu hình AuthenticationManager
        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http,
                        PasswordEncoder passwordEncoder,
                        UserDetailsService userDetailsService) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder = http
                                .getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder
                                .userDetailsService(userDetailsService)
                                .passwordEncoder(passwordEncoder);
                return authenticationManagerBuilder.build();
        }

        // Bean để cấu hình DaoAuthenticationProvider
        @Bean
        public DaoAuthenticationProvider authProvider(PasswordEncoder passwordEncoder,
                        UserDetailsService userDetailsService) {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder);
                return authProvider;
        }

        // Bean để xử lý lỗi đăng nhập
        @Bean
        public AuthenticationFailureHandler authenticationFailureHandler() {
                return new SimpleUrlAuthenticationFailureHandler("/login?error=true");
        }

        // Bean để cấu hình SecurityFilterChain
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        CustomAuthenticationSuccessHandler successHandler) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                // Cho phép các yêu cầu với DispatcherType FORWARD và INCLUDE
                                                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE)
                                                .permitAll()
                                                // Cho phép truy cập không cần xác thực vào các đường dẫn cụ thể
                                                .requestMatchers("/","/user/baiviet/chitiet/{id}","/user/baiviet/tatca","/hello", "/css/**", "/js/**", "/images/**").permitAll()
                                                .requestMatchers("/login", "/register").permitAll()
                                                // Chỉ cho phép người dùng có vai trò ADMIN truy cập vào /admin/**
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                // Chỉ cho phép người dùng có vai trò USER truy cập vào /user/**
                                                .requestMatchers("/user/**").hasRole("USER")
                                                // Yêu cầu xác thực cho tất cả các yêu cầu còn lại
                                                .anyRequest().authenticated())
                                .formLogin(login -> login
                                                // Đường dẫn đến trang đăng nhập
                                                .loginPage("/login")
                                                // Xử lý thành công đăng nhập
                                                .successHandler(successHandler)
                                                // Xử lý lỗi đăng nhập
                                                .failureHandler(authenticationFailureHandler())
                                                .permitAll())
                                .logout(logout -> logout
                                                // Đường dẫn để đăng xuất
                                                .logoutUrl("/logout")
                                                // Đường dẫn sau khi đăng xuất thành công
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }
}