package com.couplefinance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 记账请求DTO
 */
@Data
public class TransactionRequest {
    
    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    private BigDecimal amount;
    
    @NotBlank(message = "分类不能为空")
    private String category;
    
    @NotBlank(message = "类型不能为空")
    private String type;  // income / expense
    
    @NotNull(message = "付款人不能为空")
    private Long payerId;
    
    @NotNull(message = "日期不能为空")
    private LocalDate recordDate;
    
    private String note;
}
