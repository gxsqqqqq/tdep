package com.tdep.notify.service;

import com.tdep.notify.entity.NotifyMessage;

import java.util.List;

public interface NotifyService {

    List<NotifyMessage> getMessages(Long userId, int page, int size);

    long getUnreadCount(Long userId);

    void markAsRead(Long userId, Long messageId);

    void markAllAsRead(Long userId);

    void sendNotify(Long userId, String notifyType, String title, String content);
}
