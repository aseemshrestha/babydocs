import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan( basePackages = {"com.babydocs.*","com.babydocs.annotations.*" })
@EntityScan( basePackages = "com.babydocs.*" )
@EnableJpaRepositories( basePackages = {"com.babydocs.*"} )

public class BabyDocsApp
{
    public static void main(String[] args)
    {
        SpringApplication.run(BabyDocsApp.class);
    }
}
