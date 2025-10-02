package msa_webflux_gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class GatewayRouter {

    @Bean
    public RouterFunction<ServerResponse> route(GatewayHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/point/{seq}/{paymentAmount}/{industryCode}"), handler::forwardToPoint)
                .andRoute(RequestPredicates.POST("/coupon/issue"), handler::forwardToCoupon);
    }
}

