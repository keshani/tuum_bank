
CREATE TABLE ACCOUNT (
  ACCOUNT_ID VARCHAR(50) PRIMARY KEY ,
  CUSTOMER_ID VARCHAR(20) NOT NULL,
  COUNTRY VARCHAR(20) NOT NULL
);

CREATE TABLE ACCOUNT_BALANCE (
  ID BIGSERIAL PRIMARY KEY,
  ACCOUNT_ID VARCHAR(50) NOT NULL,
  CURRENCY_TYPE VARCHAR(5) NOT NULL ,
  BALANCE NUMERIC(12, 2) DEFAULT 0,
  FOREIGN KEY(ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID)
);

CREATE TABLE TRANSACTIONS (
  ID BIGSERIAL PRIMARY KEY,
  ACCOUNT_ID VARCHAR(50) NOT NULL,
  CURRENCY_TYPE VARCHAR(5) NOT NULL ,
  TRANSFER_AMOUNT NUMERIC(12, 2) DEFAULT 0,
  BALANCE NUMERIC(12, 2) DEFAULT 0,
  TRANSACTION_TYPE VARCHAR(5) NOT NULL ,
  TRANSACTION_DESC VARCHAR(100) NOT NULL ,
  TRANSACTION_DATETIME DATE NOT NULL,
  FOREIGN KEY(ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID)
);





