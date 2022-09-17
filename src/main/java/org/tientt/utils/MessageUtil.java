package org.tientt.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component

public class MessageUtil {
    private static MessageSource messageSource;

    public MessageUtil(MessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    public static String getMessage(String messageKey, Object... args) {
        if (messageSource == null)
            return "ApplicationContext unavailable, please contract to Dev team to maintain  this issue.";
        return messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
    }
}
