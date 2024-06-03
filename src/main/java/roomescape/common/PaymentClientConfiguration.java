package roomescape.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.client.PaymentProperties;
import roomescape.payment.client.TossPaymentClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class PaymentClientConfiguration {

    private static final String BASIC_PREFIX = "Basic ";
    private static final String NO_PASSWORD_SUFFIX = ":";

    private final PaymentProperties paymentProperties;
    private final String tossBaseUrl;

    public PaymentClientConfiguration(final PaymentProperties paymentProperties,
                                      @Value("${payment.baseUrl.toss}") final String tossBaseUrl) {
        this.paymentProperties = paymentProperties;
        this.tossBaseUrl = tossBaseUrl;
    }

    @Bean
    public PaymentClient tossPaymentClient() {
        byte[] encodedBytes = Base64.getEncoder()
                .encode((paymentProperties.getSecretKey() + NO_PASSWORD_SUFFIX)
                        .getBytes(StandardCharsets.UTF_8));

        String authorizations = BASIC_PREFIX + new String(encodedBytes);

        return new TossPaymentClient(
                RestClient.builder()
                        .baseUrl(tossBaseUrl)
                        .defaultHeader(HttpHeaders.AUTHORIZATION, authorizations)
                        .defaultStatusHandler(new TossPaymentResponseErrorHandler())
                        .requestFactory(getClientHttpRequestFactory())
                        .build()
        );
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(3000);
        simpleClientHttpRequestFactory.setReadTimeout(3000);
        return simpleClientHttpRequestFactory;
    }
}