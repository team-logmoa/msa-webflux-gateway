package msa_webflux_gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GatewayHandler {

    private final WebClient webClient;

    @Value("${gateway.services.point}")
    private String pointServiceUrl;

    @Value("${gateway.services.coupon}")
    private String couponServiceUrl;

    // 포인트 서버로 포워딩 (GET)
    public Mono<ServerResponse> forwardToPoint(ServerRequest request) {
        String seq = request.pathVariable("seq");
        String paymentAmount = request.pathVariable("paymentAmount");
        String industryCode = request.pathVariable("industryCode");

        return webClient.get()
                .uri(pointServiceUrl + "/point/{seq}/{paymentAmount}/{industryCode}",
                        seq, paymentAmount, industryCode)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(resBody -> ServerResponse.ok().bodyValue(resBody));
    }

    // 쿠폰 서버로 포워딩
    public Mono<ServerResponse> forwardToCoupon(ServerRequest request) {
        return request.bodyToMono(String.class) // body 그대로 받음
                .flatMap(reqBody ->
                        webClient.post()
                                .uri(couponServiceUrl + "/coupon/issue")
                                .bodyValue(reqBody) // {"seq":"..."} 그대로 전달
                                .retrieve()
                                .bodyToMono(String.class)
                                .flatMap(resBody -> ServerResponse.ok().bodyValue(resBody))
                );
    }
}
