/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Tienda.demo;

import Tienda.demo.service.RutaService;
import Tienda.demo.domain.Ruta;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.springframework.beans.factory.annotation.Autowired;


//nuevos
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.core.io.ClassPathResource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;


/**
 *
 * @author melaniebenavidesblandon
 */
@Configuration
public class ProjectConfig implements WebMvcConfigurer{
          /* Los siguiente métodos son para implementar el tema de seguridad dentro del proyecto */
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("index");
            registry.addViewController("/ejemplo2").setViewName("ejemplo2");
            registry.addViewController("/multimedia").setViewName("multimedia");
            registry.addViewController("/iframes").setViewName("iframes");
            registry.addViewController("/login").setViewName("login");
            registry.addViewController("/registro/nuevo").setViewName("/registro/nuevo");
        }

        /* El siguiente método se utilizar para publicar en la nube, independientemente  */
        @Bean
        public SpringResourceTemplateResolver templateResolver_0() {
            SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
            resolver.setPrefix("classpath:/templates");
            resolver.setSuffix(".html");
            resolver.setTemplateMode(TemplateMode.HTML);
            resolver.setOrder(0);
            resolver.setCheckExistence(true);
            return resolver;
        }
        // Son los beans para internalización 
       @Bean
       public LocaleResolver localeResolver() {
           var slr = new SessionLocaleResolver();
           slr.setDefaultLocale(Locale.getDefault());
           slr.setLocaleAttributeName("session.current.locale");
           slr.setTimeZoneAttributeName("session.current.timezone");
           return slr;
       }

       @Bean
       public LocaleChangeInterceptor localeChangeInterceptor() {
           var lci = new LocaleChangeInterceptor();
           lci.setParamName("lang");
           return lci;
       }

       @Override
       public void addInterceptors(InterceptorRegistry registro) {
           registro.addInterceptor(localeChangeInterceptor());
       }

       //Bean para poder acceder a los messages.properties en código...
       @Bean("messageSource")
       public MessageSource messageSource() {
           ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
           messageSource.setBasenames("messages");
           messageSource.setDefaultEncoding("UTF-8");
           return messageSource;
       }
       
       public static final String[] PUBLIC_URLS = {
        "/", "/index", "/fav/**", "/carrito/**", "/consultas/**", "/registro/**",
        "/js/**", "/webjars/**", "/login", "/acceso_denegado"
    };

    public static final String[] ADMIN_URLS = {
        "/producto/nuevo", "/producto/guardar", "/producto/modificar/**", "/producto/eliminar/**",
        "/categoria/nuevo", "/categoria/guardar", "/categoria/modificar/**", "/categoria/eliminar/**",
        "/usuario/nuevo", "/usuario/guardar", "/usuario/modificar/**", "/usuario/eliminar/**"
    };

    public static final String[] ADMIN_OR_VENDEDOR_URLS = {
        "/producto/listado", "/categoria/listado", "/usuario/listado"
    };

    public static final String[] USUARIO_URLS = {
        "/facturar/carrito"
    };

 
    

@Autowired
private RutaService rutaService;

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    var rutas = rutaService.getRutas();
    http.authorizeHttpRequests(requests -> {
        for (Ruta ruta : rutas) {
            if (ruta.isRequiereRol()) {
                requests.requestMatchers(ruta.getRuta()).hasRole(ruta.getRol().getRol());
            } else {
                requests.requestMatchers(ruta.getRuta()).permitAll();
            }
        }
        requests.anyRequest().authenticated();
    });
    
    http.formLogin(form -> form // Configuración de formulario de login
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/", true)
            .failureUrl("/login?error=true")
            .permitAll()
    )
    .logout(logout -> logout // Configuración de logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout=true")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
    )
    .exceptionHandling(exceptions -> exceptions // Manejo de excepciones
            .accessDeniedPage("/acceso_denegado")
    )
    .sessionManagement(session -> session // Configuración de sesiones
            .maximumSessions(1)
            .maxSessionsPreventsLogin(false)
    );
    
    return http.build();
}
     

  @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    

}