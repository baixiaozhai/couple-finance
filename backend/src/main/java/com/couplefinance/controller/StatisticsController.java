package com.couplefinance.controller;

import com.couplefinance.dto.ApiResponse;
import com.couplefinance.dto.StatisticsResponse;
import com.couplefinance.security.UserPrincipal;
import com.couplefinance.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取月度统计
     */
    @GetMapping("/month")
    public ResponseEntity<ApiResponse<StatisticsResponse>> getMonthlyStatistics(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String month) {
        return ResponseEntity.ok(statisticsService.getMonthlyStatistics(userPrincipal.getUserId(), month));
    }
}
