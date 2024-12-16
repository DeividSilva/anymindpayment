CREATE SEQUENCE COURIER_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE CASH START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE COURIER_RULE_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE RULE_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE PAYMENT_METHOD_SEQ START WITH 1 INCREMENT BY 1;

INSERT INTO COURIER (ID, NAME, DESCRIPTION) VALUES
                                                (NEXTVAL('COURIER_SEQ'), 'YAMATO', 'Courier for Yamato Transport'),
                                                (NEXTVAL('COURIER_SEQ'), 'SAGAWA', 'Courier for Sagawa Express');

INSERT INTO RULE (ID, NAME, STORE_CREDIT_CARD_LAST_FOUR, STORE_BANK_CODE, STORE_ACCOUNT_NUMBER, STORE_CHECK_NUMBER, VALIDATE_COURIER, DESCRIPTION)
VALUES
    (NEXTVAL('RULE_SEQ'), 'CASH', FALSE, FALSE, FALSE, FALSE, FALSE, 'Payment made in cash without storing sensitive information.'),
    (NEXTVAL('RULE_SEQ'), 'ON_DELIVERY', FALSE, FALSE, FALSE, FALSE, TRUE, 'Payment upon delivery without storing sensitive information.'),
    (NEXTVAL('RULE_SEQ'), 'CREDIT_CARD', TRUE, FALSE, FALSE, FALSE, FALSE, 'Payment via credit card storing the last four digits.'),
    (NEXTVAL('RULE_SEQ'), 'ONLINE_PAYMENT', FALSE, FALSE, FALSE, FALSE, FALSE, 'Payment processed online without storing sensitive information.'),
    (NEXTVAL('RULE_SEQ'), 'BANK_TRANSFER', FALSE, TRUE, FALSE, FALSE, FALSE, 'Payment via bank transfer storing banking details.'),
    (NEXTVAL('RULE_SEQ'), 'CHEQUE', FALSE, TRUE, FALSE, TRUE, FALSE, 'Payment via cheque storing bank and cheque details.');

INSERT INTO COURIER_RULE (ID, ID_RULE, ID_COURIER, DESCRIPTION)
SELECT NEXTVAL('COURIER_RULE_SEQ'),
       (SELECT ID FROM RULE WHERE NAME = 'ON_DELIVERY'),
       (SELECT ID FROM COURIER WHERE NAME = 'YAMATO'),
       'Rule for cash payments handled by Yamato courier.';

INSERT INTO COURIER_RULE (ID, ID_RULE, ID_COURIER, DESCRIPTION)
SELECT NEXTVAL('COURIER_RULE_SEQ'),
       (SELECT ID FROM RULE WHERE NAME = 'ON_DELIVERY'),
       (SELECT ID FROM COURIER WHERE NAME = 'SAGAWA'),
       'Rule for credit card payments handled by Sagawa courier.';
