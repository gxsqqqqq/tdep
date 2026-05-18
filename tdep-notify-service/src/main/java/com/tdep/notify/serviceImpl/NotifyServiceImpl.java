package com.tdep.notify.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.notify.entity.NotifyMessage;
import com.tdep.notify.mapper.NotifyMessageMapper;
import com.tdep.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final NotifyMessageMapper notifyMessageMapper;

    @Override
    public List<NotifyMessage> getMessages(Long userId, int page, int size) {
        Page<NotifyMessage> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<NotifyMessage> wrapper = new LambdaQueryWrapper<NotifyMessage>()
                .eq(NotifyMessage::getUserId, userId)
                .orderByDesc(NotifyMessage::getCreatedAt);
        return notifyMessageMapper.selectPage(pageParam, wrapper).getRecords();
    }

    @Override
    public long getUnreadCount(Long userId) {
        LambdaQueryWrapper<NotifyMessage> wrapper = new LambdaQueryWrapper<NotifyMessage>()
                .eq(NotifyMessage::getUserId, userId)
                .eq(NotifyMessage::getStatus, "UNREAD");
        return notifyMessageMapper.selectCount(wrapper);
    }

    @Override
    @Transactional
    public void markAsRead(Long userId, Long messageId) {
        NotifyMessage message = notifyMessageMapper.selectById(messageId);
        if (message == null || !message.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "消息不存在");
        }
        if ("UNREAD".equals(message.getStatus())) {
            LambdaUpdateWrapper<NotifyMessage> wrapper = new LambdaUpdateWrapper<NotifyMessage>()
                    .eq(NotifyMessage::getId, messageId)
                    .set(NotifyMessage::getStatus, "READ")
                    .set(NotifyMessage::getReadAt, LocalDateTime.now());
            notifyMessageMapper.update(null, wrapper);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        LambdaUpdateWrapper<NotifyMessage> wrapper = new LambdaUpdateWrapper<NotifyMessage>()
                .eq(NotifyMessage::getUserId, userId)
                .eq(NotifyMessage::getStatus, "UNREAD")
                .set(NotifyMessage::getStatus, "READ")
                .set(NotifyMessage::getReadAt, LocalDateTime.now());
        notifyMessageMapper.update(null, wrapper);
    }

    @Override
    @Transactional
    public void sendNotify(Long userId, String notifyType, String title, String content) {
        NotifyMessage message = new NotifyMessage();
        message.setUserId(userId);
        message.setNotifyType(notifyType);
        message.setTitle(title);
        message.setContent(content);
        message.setStatus("UNREAD");
        notifyMessageMapper.insert(message);
    }
}
