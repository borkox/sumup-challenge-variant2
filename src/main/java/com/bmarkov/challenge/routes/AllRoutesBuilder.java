package com.bmarkov.challenge.routes;

import com.bmarkov.challenge.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.BeanInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import static java.util.Arrays.stream;

@Slf4j
public class AllRoutesBuilder extends RouteBuilder {

    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .setBody(simple("{\"error\": \"${exception.getMessage()}\"}"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("500"));

        from("direct:tasksInOrder")
                .routeId("tasksInOrder")
                .to("bean:jobService?method=tasksInOrder")

                // clear field dependencies
                .process(e -> {
                    Task[] tasks = ((Task[]) e.getIn().getBody());
                    stream(tasks).forEach(t -> t.setRequires(null));
                })
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"));

        from("direct:tasksAsBash")
                .routeId("tasksAsBash")
                // reuse route
                .to("direct:tasksInOrder")
                .to("bean:bashScriptService?method=tasksAsBashScript")
                .setHeader("Content-Type", constant("text/plain"))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, simple("200"));


    }


}
