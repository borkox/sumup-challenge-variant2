package com.bmarkov.challenge.routes;

import com.bmarkov.challenge.model.Job;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.BeanInject;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.cdi.CdiCamelConfiguration;
import org.apache.camel.cdi.Uri;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.event.CamelContextStartingEvent;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.reifier.RouteReifier;
import org.apache.camel.test.cdi.CamelCdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.event.Observes;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.apache.commons.io.IOUtils.resourceToString;

@RunWith(CamelCdiRunner.class)
public class AllRoutesBuilderTest {

    @BeanInject
    CamelContext camelContext;

    @BeanInject
    AllRoutesBuilder allRoutesBuilder;
    private ObjectMapper mapper;

    static void configuration(@Observes CdiCamelConfiguration configuration) {
        configuration.autoConfigureRoutes(false);
    }
    void advice(@Observes CamelContextStartingEvent event,
                ModelCamelContext context) throws Exception {

        camelContext.addRoutes(allRoutesBuilder);

        RouteReifier.adviceWith(context.getRouteDefinition("tasksAsBash"), camelContext,new AdviceWithRouteBuilder() {
            @Override
            public void configure() {
                weaveAddLast().to("mock:out");
            }
        });

    }

    @Test
    public void testBashGeneration(
            @Uri("direct:tasksAsBash") ProducerTemplate in,
            @Uri("mock:out") MockEndpoint out)
            throws IOException, InterruptedException {
        String request = resourceToString("/example.json", Charset.forName("UTF-8"));
        String expected = resourceToString("/expected_bash.txt", Charset.forName("UTF-8"));
        expected = expected.replaceAll("\r", "");

        out.expectedMessageCount(1);
        out.expectedBodiesReceived(expected);
        mapper = new ObjectMapper();
        in.sendBody(mapper.readValue(request, Job.class));

        out.assertIsSatisfied();

    }
}
