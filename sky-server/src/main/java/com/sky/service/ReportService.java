package com.sky.service;

/**
 * @author 周超
 * @version 1.0
 */
import com.sky.vo.TurnoverReportVO;
import java.time.LocalDate;

public interface ReportService {

    /**
     * 根据指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}
