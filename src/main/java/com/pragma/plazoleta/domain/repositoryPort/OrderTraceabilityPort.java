package com.pragma.plazoleta.domain.repositoryPort;

import com.pragma.plazoleta.domain.model.OrderTraceLog;

public interface OrderTraceabilityPort {
    void saveOrderTrace(OrderTraceLog orderTraceLog);
}

