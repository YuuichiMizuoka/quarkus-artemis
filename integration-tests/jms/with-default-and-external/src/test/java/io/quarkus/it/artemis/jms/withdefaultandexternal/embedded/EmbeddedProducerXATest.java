package io.quarkus.it.artemis.jms.withdefaultandexternal.embedded;

import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;

import io.quarkus.artemis.test.ArtemisTestResource;
import io.quarkus.it.artemis.jms.withdefaultandexternal.BaseArtemisProducerXATest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.jms.JMSContext;

@QuarkusTest
@QuarkusTestResource(ArtemisTestResource.class)
@QuarkusTestResource(NamedOneArtemisTestResource.class)
@QuarkusTestResource(ExternallyDefinedArtemisTestResource.class)
class EmbeddedProducerXATest extends BaseArtemisProducerXATest {
    @Test
    void testXAExternallyDefined() throws Exception {
        testXA(createExternallyDefinedContext(), "/artemis/externally-defined/xa", "test-jms-externally-defined");
    }

    @Test
    void testRollbackExternallyDefined() {
        testRollback(createNamedOneContext(), "/artemis/externally-defined/xa", "test-jms-externally-defined");
    }

    private JMSContext createExternallyDefinedContext() {
        String url = ConfigProvider.getConfig().getValue("artemis.externally-defined.url", String.class);
        return new ActiveMQJMSConnectionFactory(url).createContext(JMSContext.AUTO_ACKNOWLEDGE);
    }
}
