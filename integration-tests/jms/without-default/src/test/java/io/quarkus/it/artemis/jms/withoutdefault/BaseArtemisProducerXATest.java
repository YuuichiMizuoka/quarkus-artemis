package io.quarkus.it.artemis.jms.withoutdefault;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import jakarta.jms.JMSConsumer;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;

public abstract class BaseArtemisProducerXATest extends BaseArtemisProducerTest {
    @Test
    void testXANamedOne() throws Exception {
        testXA(createNamedOnContext(), "/artemis/named-1/xa", "test-jms-named-1");
    }

    private void testXA(JMSContext context, String endpoint, String queueName) throws JMSException {
        String body = createBody();
        Response response = RestAssured.with().body(body).post(endpoint);
        Assertions.assertEquals(jakarta.ws.rs.core.Response.Status.NO_CONTENT.getStatusCode(), response.statusCode());

        try (JMSContext autoClosedContext = context) {
            JMSConsumer consumer = autoClosedContext.createConsumer(autoClosedContext.createQueue(queueName));
            Message message = consumer.receive(1000L);
            Assertions.assertEquals(body, message.getBody(String.class));
        }
    }

    @Test
    public void testRollbackNamedOne() {
        testRollback(createNamedOnContext(), "/artemis/named-1/xa", "test-jms-named-1");
    }

    private void testRollback(JMSContext context, String endpoint, String queueName) {
        Response response = RestAssured.with().body("fail").post(endpoint);
        Assertions.assertEquals(jakarta.ws.rs.core.Response.Status.NO_CONTENT.getStatusCode(), response.statusCode());

        try (JMSContext autoClosedContext = context) {
            JMSConsumer consumer = autoClosedContext.createConsumer(autoClosedContext.createQueue(queueName));
            Message message = consumer.receive(1000L);
            Assertions.assertNull(message);
        }
    }
}
