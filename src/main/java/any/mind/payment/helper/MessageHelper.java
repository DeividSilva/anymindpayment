package any.mind.payment.helper;

import any.mind.payment.domain.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageHelper {

    private final MessageSource messageSource;

    public String get(final Message message, final Object... args) {
        if (message == null) {
            return "";
        }

        return this.messageSource.getMessage(message.getDescricao(), args, Locale.getDefault());
    }

}