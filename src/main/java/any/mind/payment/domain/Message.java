package any.mind.payment.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = PRIVATE)
public enum Message {

    INVALID_PAYMENT_METHOD("error.invalid.payment.method"),
    INVALID_PRICE_MODIFIER("error.invalid.price.modifier"),
    PROVIDE_BANK_CODE("error.provide.bank.code"),
    PROVIDE_ACCOUNT_NUMBER("error.provide.account.number"),
    PROVIDE_CHECK_NUMBER("error.provide.check.number"),
    PROVIDE_LAST_FOUR("error.provide.last.four"),
    INVALID_LAST_FOUR("error.invalid.last.four"),
    PROVIDE_COURIER("error.provide.courier"),
    INVALID_COURIER("error.invalid.courier"),
    INVALID_REQUEST_FIELD("error.invalid.request.field"),
    ;

    private final String descricao;

}