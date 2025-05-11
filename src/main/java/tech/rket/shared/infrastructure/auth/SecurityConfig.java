package tech.rket.shared.infrastructure.auth;

import jakarta.servlet.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.debug.DebugFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CompositeFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationFilter authFilter;
    private final RouteAuthorizationFilter routeAuthorizationFilter;

    @Bean
    static BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor() {
        return registry -> registry.getBeanDefinition(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME).setBeanClassName(CompositeFilterChainProxy.class.getName());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return config(http).build();
    }

    protected HttpSecurity config(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(new UserDetailServiceImpl());
        AuthenticationManager authenticationManager = auth.build();

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authenticationManager(authenticationManager);
        http.authorizeHttpRequests(customizer -> customizer
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers(routeAuthorizationFilter).permitAll()
                .anyRequest().permitAll()
        );
        http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        return http;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    static class CompositeFilterChainProxy extends FilterChainProxy {

        private final Filter doFilterDelegate;

        private final FilterChainProxy springSecurityFilterChain;

        CompositeFilterChainProxy(List<? extends Filter> filters) {
            this.doFilterDelegate = createDoFilterDelegate(filters);
            this.springSecurityFilterChain = findFilterChainProxy(filters);
        }

        @Override
        public void afterPropertiesSet() {
            this.springSecurityFilterChain.afterPropertiesSet();
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            this.doFilterDelegate.doFilter(request, response, chain);
        }

        @Override
        public List<Filter> getFilters(String url) {
            return this.springSecurityFilterChain.getFilters(url);
        }

        @Override
        public List<SecurityFilterChain> getFilterChains() {
            return this.springSecurityFilterChain.getFilterChains();
        }

        @Override
        public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
            this.springSecurityFilterChain.setSecurityContextHolderStrategy(securityContextHolderStrategy);
        }

        @Override
        public void setFilterChainValidator(FilterChainValidator filterChainValidator) {
            this.springSecurityFilterChain.setFilterChainValidator(filterChainValidator);
        }

        @Override
        public void setFilterChainDecorator(FilterChainDecorator filterChainDecorator) {
            this.springSecurityFilterChain.setFilterChainDecorator(filterChainDecorator);
        }

        @Override
        public void setFirewall(HttpFirewall firewall) {
            this.springSecurityFilterChain.setFirewall(firewall);
        }

        @Override
        public void setRequestRejectedHandler(RequestRejectedHandler requestRejectedHandler) {
            this.springSecurityFilterChain.setRequestRejectedHandler(requestRejectedHandler);
        }

        private static Filter createDoFilterDelegate(List<? extends Filter> filters) {
            CompositeFilter delegate = new CompositeFilter();
            delegate.setFilters(filters);
            return delegate;
        }

        private static FilterChainProxy findFilterChainProxy(List<? extends Filter> filters) {
            for (Filter filter : filters) {
                if (filter instanceof FilterChainProxy fcp) {
                    return fcp;
                }
                if (filter instanceof DebugFilter debugFilter) {
                    return debugFilter.getFilterChainProxy();
                }
            }
            throw new IllegalStateException("Couldn't find FilterChainProxy in " + filters);
        }

    }
}
