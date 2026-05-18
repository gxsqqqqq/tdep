package com.tdep.notify.controller;

import com.tdep.common.api.Result;
import com.tdep.common.enums.ResultCode;
import com.tdep.common.exception.BusinessException;
import com.tdep.common.security.SecurityContextUtil;
import com.tdep.notify.entity.NotifyMessage;
import com.tdep.notify.service.NotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/notify", "/notify"})
@RequiredArgsConstructor
public class NotifyController {

    private final NotifyService notifyService;

    @GetMapping("/messages")
    public Result<List<NotifyMessage>> getMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = getCurrentUserId();
        List<NotifyMessage> messages = notifyService.getMessages(userId, page, size);
        return Result.success(messages);
    }

    @GetMapping("/messages/unread-count")
    public Result<Map<String, Long>> getUnreadCount() {
        Long userId = getCurrentUserId();
        long count = notifyService.getUnreadCount(userId);
        return Result.success(Map.of("count", count));
    }

    @PostMapping("/messages/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        notifyService.markAsRead(userId, id);
        return Result.success();
    }

    @PostMapping("/messages/read-all")
    public Result<Void> markAllAsRead() {
        Long userId = getCurrentUserId();
        notifyService.markAllAsRead(userId);
        return Result.success();
    }

    private Long getCurrentUserId() {
        return SecurityContextUtil.currentUserId()
                .orElseThrow(() -> new BusinessException(ResultCode.UNAUTHORIZED));
    }
}
