package com.wertyxa.binance_p2p_parser;

import com.wertyxa.binance_p2p_parser.enums.CryptoCurrency;
import com.wertyxa.binance_p2p_parser.enums.FiatCurrency;
import com.wertyxa.binance_p2p_parser.enums.PayTypes;
import com.wertyxa.binance_p2p_parser.enums.TradeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Slf4j
public class BinanceP2pParserApplication {

    public static void main(String[] args) {
        SpringApplication.run(BinanceP2pParserApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(P2PBinanceRateParser parser) {
        return args -> {
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                List<BigDecimal> lastRate = parser.getLastRate(FiatCurrency.UAH, CryptoCurrency.USDT, List.of(PayTypes.Monobank), TradeType.BUY);
                OptionalDouble average = lastRate.stream().mapToDouble(BigDecimal::doubleValue).average();
                if (average.isPresent()) {
                    log.info("avg rate {}", BigDecimal.valueOf(average.getAsDouble()).setScale(2, RoundingMode.CEILING));
                }
            }, 0,5, TimeUnit.SECONDS);
        };
    }
}
