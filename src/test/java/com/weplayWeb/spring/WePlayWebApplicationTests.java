package com.weplayWeb.spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@TestPropertySource(properties = {
    "MAIL_HOST=smtpout.secureserver.net",
    "MAIL_PORT=587",
    "MAIL_USERNAME=contactus@weplayofficial.com",
    "MAIL_PASSWORD=dldzzvnljlwfmpxj",
    "spring.mail.properties.mail.smtp.auth=true",
    "spring.mail.properties.mail.smtp.starttls.enable=true",
   "spring.mail.properties.mail.smtp.starttls.required=true",
    "spring.mail.properties.mail.smtp.ssl.trust=smtpout.secureserver.net",
    "spring.mail.properties.mail.debug=true"

})
class WePlayWebApplicationTests {

	@Test
	void contextLoads() {
	}

}
