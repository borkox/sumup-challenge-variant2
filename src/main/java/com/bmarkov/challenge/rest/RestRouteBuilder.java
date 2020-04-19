package com.bmarkov.challenge.rest;

import com.bmarkov.challenge.model.Job;
import org.apache.camel.BeanInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class RestRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {

        restConfiguration().component("undertow")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .contextPath("/").host("0.0.0.0").port(8080)
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Jobs API")
                .apiProperty("api.version", "1.0.0")
                .apiProperty("cors", "true");

        rest("/api/job/tasksInOrder")
                .description("Service for jobs")
                .consumes("application/json")
                .produces("application/json")
                .clientRequestValidation(true)
                .post("")
                .type(Job.class)
                .description("Returns tasks in proper order")
                .to("direct:tasksInOrder");

        rest("/api/job/tasksAsBash")
                .description("Create bash script")
                .consumes("application/json")
                .clientRequestValidation(true)
                .produces("text/plain")
                .post("")
                .type(Job.class)
                .description("Returns tasks as bash script")
                .to("direct:tasksAsBash") ;

    }

}
