package com.couplefinance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 预算请求DTO
 */
@Data
public class BudgetRequest {
    
    @NotBlank(message = "月份不能为空")
    private String month;  // 格式：2026-04
    
    @NotBlank(message = "分类不能为空")
    private String category;
    
    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    private BigDecimal amount;
}
