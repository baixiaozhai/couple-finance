package com.couplefinance.service;

import com.couplefinance.dto.ApiResponse;
import com.couplefinance.dto.StatisticsResponse;

/**
 * 统计服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取月度统计
     */
    ApiResponse<StatisticsResponse> getMonthlyStatistics(Long userId, String month);
}
