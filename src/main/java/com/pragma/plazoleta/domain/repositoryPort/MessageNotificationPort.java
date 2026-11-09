package com.pragma.plazoleta.domain.repositoryPort;

public interface MessageNotificationPort {
    void sendMessage(String destination, String message);
}
