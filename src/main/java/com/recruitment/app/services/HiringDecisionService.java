package com.recruitment.app.services;

import com.recruitment.app.models.Application;
import java.util.List;
import java.util.Map;

public interface HiringDecisionService {
    boolean makeHiringDecision(Long applicationId, String decision, Long hiringManagerId, String notes);
    boolean bulkUpdateHiringDecisions(Map<Long, String> decisions, Long hiringManagerId);
    List<Application> getApplicationsByDecision(Long hiringManagerId, String decision);
    Map<String, Integer> getHiringStatistics(Long hiringManagerId);
    boolean revertHiringDecision(Long applicationId, Long hiringManagerId);
}