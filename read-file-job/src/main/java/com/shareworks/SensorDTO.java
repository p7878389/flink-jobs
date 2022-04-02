package com.shareworks;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author martin.peng
 */
@Data
public class SensorDTO {

    private String id;

    private String timestamp;

    private String temperature;
}
