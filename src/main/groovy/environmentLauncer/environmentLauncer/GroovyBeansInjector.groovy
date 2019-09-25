package environmentLauncer.environmentLauncer

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import services.BundleManager
import services.MdataManager
import services.PropertiesResolver
import services.Wso2UsersManager

@Configuration
class GroovyBeansInjector {

    @Autowired
    PropertiesResolver propertiesResolver

    @Bean
    PropertiesResolver createPropertiesResolver() {
        new PropertiesResolver()
    }

    @Bean
    BundleManager createBundleManager() {
        new BundleManager()
    }

    @Bean
    MdataManager createMdataManager() {
        new MdataManager()
    }

    @Bean
    Wso2UsersManager createWso2UsersManager() {
        new Wso2UsersManager()
    }

}
