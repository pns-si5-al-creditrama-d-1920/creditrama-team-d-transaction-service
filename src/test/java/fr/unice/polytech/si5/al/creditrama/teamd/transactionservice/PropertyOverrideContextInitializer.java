package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;

public class PropertyOverrideContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext, "service.bankaccount=none");
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext, "security.oauth2.resource.access-token-uri=none");
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext, "security.oauth2.resource.token-info-uri=none");
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext, "security.oauth2.client.client-id=none");
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(configurableApplicationContext, "security.oauth2.client.client-secret=none");
    }
}
