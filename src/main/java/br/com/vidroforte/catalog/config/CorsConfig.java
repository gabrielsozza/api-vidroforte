package br.com.vidroforte.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permite credenciais
        config.setAllowCredentials(true);
        
        // Permite requisições de qualquer origem (desenvolvimento)
        // Em produção, especifique apenas os domínios permitidos
        config.addAllowedOriginPattern("*");
        
        // Permite todos os headers
        config.addAllowedHeader("*");
        
        // Permite todos os métodos HTTP
        config.addAllowedMethod("*");
        
        // Registra a configuração para todas as rotas
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
