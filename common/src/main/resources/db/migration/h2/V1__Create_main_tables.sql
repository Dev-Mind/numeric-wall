CREATE TABLE Authority
(
    ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    NAME VARCHAR(255) NOT NULL
);
CREATE TABLE Category
(
    ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    CREATEDAT TIMESTAMP,
    NAME VARCHAR(255) NOT NULL,
    CODE VARCHAR(50),
    MESSAGE VARCHAR(255),
    SHORTCODE VARCHAR(5)
);
CREATE TABLE QrCode
(
    ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    GENERATEDAT TIMESTAMP,
    CATEGORY_ID BIGINT NOT NULL,
    PRODUCTION_ID BIGINT,
    URL VARCHAR(512) NOT NULL,
    X BIGINT,
    Y BIGINT,
    DIMENSION BIGINT,
    BIG BOOLEAN,
    SVGPATH CLOB
);

CREATE TABLE User
(
    ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    ESMEID VARCHAR(255),
    token VARCHAR(255),
    FIRSTCONNECTIONAT TIMESTAMP
);
CREATE TABLE User_Authority
(
    USER_ID BIGINT NOT NULL,
    AUTHORITIES_ID BIGINT NOT NULL,
    PRIMARY KEY (USER_ID, AUTHORITIES_ID)
);
CREATE TABLE Production
(
    ID BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    CONTENT CLOB,
    CREATEDAT TIMESTAMP,
    USER_ID BIGINT NOT NULL,
    USERMAJ VARCHAR(255),
    STATE VARCHAR(255) NOT NULL
);

ALTER TABLE QrCode ADD FOREIGN KEY (CATEGORY_ID) REFERENCES Category (ID);
ALTER TABLE QrCode ADD FOREIGN KEY (PRODUCTION_ID) REFERENCES Production (ID);
CREATE INDEX FK_3YONWMFK6VPSHB0N2CC0TOYFH_INDEX_8 ON QrCode (PRODUCTION_ID);
CREATE INDEX FK_LRXL47CEVKKUIG3KYMJD791KR_INDEX_8 ON QrCode (CATEGORY_ID);
ALTER TABLE User_Authority ADD FOREIGN KEY (AUTHORITIES_ID) REFERENCES Authority (ID);
ALTER TABLE User_Authority ADD FOREIGN KEY (USER_ID) REFERENCES User (ID);
CREATE INDEX FK_S5AERDIQYT5E9QREWACJS4MSS_INDEX_B ON User_Authority (AUTHORITIES_ID);
ALTER TABLE Production ADD FOREIGN KEY (USER_ID) REFERENCES User (ID);
CREATE INDEX FK_FFEVXL8SDHYH17NE0BOSP6XE8_INDEX_2 ON Production (USER_ID);
