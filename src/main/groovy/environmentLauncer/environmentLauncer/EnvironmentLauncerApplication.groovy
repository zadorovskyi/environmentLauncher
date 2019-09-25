package environmentLauncer.environmentLauncer

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import services.Wso2DeleteUsers

@SpringBootApplication
class EnvironmentLauncerApplication {

	static void main(String[] args) {
		SpringApplication.run EnvironmentLauncerApplication, args
	}
}
