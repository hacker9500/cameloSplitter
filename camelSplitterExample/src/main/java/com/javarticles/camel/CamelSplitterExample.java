package com.javarticles.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.util.jndi.JndiContext;

public class CamelSplitterExample {
	public static final void main(String[] args) throws Exception {
	    JndiContext jndiContext = new JndiContext();
	    jndiContext.bind("processOrderItem", new OrderItemProcessor());
        CamelContext camelContext = new DefaultCamelContext(jndiContext);
        try {
            camelContext.addRoutes(new RouteBuilder() {
                public void configure() {
                    from("timer://generateOrders?fixedRate=true&period=10000")
                    .log("Generate order items")
                    .process(new Processor() {
                        
                        public void process(Exchange exchange) throws Exception {
                            Order order = new Order(1);
                            order.addItem("Coke", 20, 2);
                            order.addItem("Banana", 12, 1);
                            order.addItem("Tab", 5, 250);
                            exchange.getIn().setBody(order);
                        }
                    })
                    .to("direct:processOrder");
                    
                    from("direct:processOrder")
                    .log("Process order ${body}")
                    .split(simple("${body.items}"))
                    .to("direct:processOrderItem")
                    .log("Processing done ${body}")
                    .end()
                    .log("Order processed: ${body}");
                    
                    from("direct:processOrderItem")
                    .log("Processing item ${body.itemName}")
                    .to("bean:processOrderItem");
                    
                }
            });
            camelContext.start();
            Thread.sleep(5000);
        } finally {
            camelContext.stop();
        }
	}
}
