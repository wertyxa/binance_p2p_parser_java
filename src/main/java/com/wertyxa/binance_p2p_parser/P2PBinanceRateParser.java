package com.wertyxa.binance_p2p_parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wertyxa.binance_p2p_parser.dto.ResponseData;
import com.wertyxa.binance_p2p_parser.enums.CryptoCurrency;
import com.wertyxa.binance_p2p_parser.enums.FiatCurrency;
import com.wertyxa.binance_p2p_parser.enums.PayTypes;
import com.wertyxa.binance_p2p_parser.enums.TradeType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.*;
import java.util.zip.GZIPInputStream;

@Component
@RequiredArgsConstructor
public class P2PBinanceRateParser {
    private final ObjectMapper mapper;
    private final RestClient client = RestClient.builder()
            .defaultHeaders(httpHeaders -> {
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                httpHeaders.set("Accept-Encoding", "gzip, deflate, br");
                httpHeaders.set("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:126.0) Gecko/20100101 Firefox/126.0");
            })
            .baseUrl("https://p2p.binance.com/bapi/c2c/v2/friendly/c2c/adv/search")
            .build();

    private final Map<String, Object> requestDto = new HashMap<>() {{
        put("page", 1);
        put("rows", 7);
        put("countries", List.of("all"));
        put("proMerchantAds", false);
        put("shieldMerchantAds", false);
        put("filterType", "all");
        put("periods", Collections.emptyList());
        put("additionalKycVerifyFilter", 0);
        put("publisherType", null);
        put("classifies", List.of("mass", "profession"));
    }};


    public List<BigDecimal> getLastRate(FiatCurrency fiat, CryptoCurrency crypto, List<PayTypes> payTypes, TradeType tradeType) {
        ResponseData orders = getOrders(fiat, tradeType, crypto, payTypes);
        return orders.getData().stream()
                .map(ResponseData.AdvertisementData::getPrice)
                .toList();
    }

    private ResponseData getOrders(FiatCurrency fiat, TradeType tradeType, CryptoCurrency crypto, List<PayTypes> payTypes) {
        requestDto.put("fiat", fiat.name());
        requestDto.put("tradeType", tradeType.name());
        requestDto.put("asset", crypto.name());
        requestDto.put("payTypes", payTypes);
        return  client.post()
                .body(requestDto)
                .exchange((req, res)-> mapper.readValue(new GZIPInputStream(res.getBody()), ResponseData.class));
    }
}
