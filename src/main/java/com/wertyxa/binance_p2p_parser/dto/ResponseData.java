package com.wertyxa.binance_p2p_parser.dto;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ResponseData {
    private List<AdvertisementData> data;
    @Data
    public static class AdvertisementData {
        private Advertisement adv;
        public BigDecimal getPrice(){
            return adv.getPrice();
        }
    }
    @Data
    @ToString
    public static class Advertisement {
        private BigDecimal price;
    }

}
