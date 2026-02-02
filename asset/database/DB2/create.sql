-- 1. IDOC 테이블 생성
-- 1. 테이블 생성
CREATE TABLE "TESTDB"."IDOC" (
                                 "LINEID"      DECIMAL(10, 0) NOT NULL,
                                 "IDOCTYPID"   DECIMAL(10, 0),
                                 "STATE"       DECIMAL(10, 0) DEFAULT -1,
                                 "ERRORCODE"   DECIMAL(10, 0) DEFAULT 0,
                                 "SOURCE"      DECIMAL(10, 0),
                                 "DESTINATION" DECIMAL(10, 0),
                                 "TIDID"       DECIMAL(10, 0) DEFAULT 0,
                                 "DOCNUM"      VARCHAR(16),
                                 "QUEUENAME"   VARCHAR(24),
                                 "PARTNERTYPE" VARCHAR(2),
                                 "PARTNERNAME" VARCHAR(10),
                                 "PARTNERPORT" VARCHAR(10),
                                 "MSGVARIANT"  VARCHAR(3),
                                 "ARCKEY"      VARCHAR(70),
                                 "DTIMECRE"    TIMESTAMP,
                                 "DTIMEMOD"    TIMESTAMP DEFAULT CURRENT TIMESTAMP,
                                 "USRMOD"      VARCHAR(30),
                                 "PGMMOD"      VARCHAR(30),
                                 "MODCNT"      DECIMAL(10, 0) DEFAULT 0,
                                 PRIMARY KEY ("LINEID")
);

-- 2. H2ORDERD 테이블 생성
-- 'ORDER'는 SQL 예약어이므로 DB2에서 컬럼명으로 사용할 때 주의가 필요합니다.
CREATE TABLE H2ORDERD (
    LINEID      DECIMAL(15, 0) NOT NULL,
    "ORDER"     VARCHAR(50),
    RRN         DECIMAL(15, 0),
    MACHINE     VARCHAR(50),
    PARTID      VARCHAR(50),
    LOT         DECIMAL(15, 0),
    QTY         DECIMAL(15, 5),
    PRIMARY KEY (LINEID)
);

-- 3. H2TRANS 테이블 생성
CREATE TABLE H2TRANS (
    LINEID      DECIMAL(15, 0) NOT NULL,
    TRANSTYPE   DECIMAL(5, 0),
    "ORDER"     VARCHAR(50),
    RRN         DECIMAL(15, 0),
    QTY         DECIMAL(15, 5),
    PRIMARY KEY (LINEID)
);