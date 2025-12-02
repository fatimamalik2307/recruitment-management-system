package com.recruitment.app.services;

import com.recruitment.app.models.Application;
import java.util.List;

public interface NotificationService {
    void notifyCandidates(List<Application> finalRankings);
}
