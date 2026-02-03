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

CREATE TABLE "TESTDB"."H2ORDERM" (
                                     "LINEID"      DECIMAL(10, 0) NOT NULL,
                                     "IDOCID"      DECIMAL(10, 0) NOT NULL,
                                     "DTIMECRE"    TIMESTAMP,
                                     "DTIMEMOD"    TIMESTAMP DEFAULT CURRENT TIMESTAMP,
                                     "USRMOD"      VARCHAR(30),
                                     "PGMMOD"      VARCHAR(30),
                                     "MODCNT"      DECIMAL(10, 0) DEFAULT 0,-- 여기까지가 공통 테이블
                                     "DATACODE"	   DECIMAL(10, 0),
                                     "BOOKCTRL"    DECIMAL(10, 0),
                                     "CCLIENT"     VARCHAR(30),
                                     "CORDERID"    VARCHAR(30),
                                     "CORDERTY"    VARCHAR(30),
                                     "CDTPICK"     VARCHAR(30),
                                     "CORDERPRIO"  DECIMAL(10, 0),
                                     "CTCODE"      VARCHAR(30),
                                     "CLOCID"      VARCHAR(30),
                                     "CWCID"       VARCHAR(30),
                                     "CGALID"      DECIMAL(10, 0),
                                     "CGALWHS"     VARCHAR(30),
                                     "CHOSTUSR"    VARCHAR(30),
                                     "CUSRNO"      VARCHAR(30),
                                     PRIMARY KEY ("LINEID")
);

CREATE TABLE "TESTDB"."H2ORDERD" (
                                     "LINEID"      DECIMAL(10, 0) NOT NULL,
                                     "IDOCID"      DECIMAL(10, 0) NOT NULL,
                                     "DTIMECRE"    TIMESTAMP,
                                     "DTIMEMOD"    TIMESTAMP DEFAULT CURRENT TIMESTAMP,
                                     "USRMOD"      VARCHAR(30),
                                     "PGMMOD"      VARCHAR(30),
                                     "MODCNT"      DECIMAL(10, 0) DEFAULT 0,-- 여기까지가 공통 테이블
                                     "DATACODE"	   DECIMAL(10, 0),
                                     "CCLIENT"     VARCHAR(30),
                                     "CORDERID"    VARCHAR(30),
                                     "CORDERTY"    VARCHAR(30),
                                     "CORDERLN"    DECIMAL(10, 0),
                                     "CCOID"       VARCHAR(30),
                                     "CCOTY"       VARCHAR(30),
                                     "CZONE"       VARCHAR(30),
                                     "CDRIVINGPROFILE" VARCHAR(30),
                                     PRIMARY KEY ("LINEID")
);

CREATE TABLE "TESTDB"."H2TRANS" (
                                    "LINEID"      DECIMAL(10, 0) NOT NULL,
                                    "IDOCID"      DECIMAL(10, 0) NOT NULL,
                                    "DTIMECRE"    TIMESTAMP,
                                    "DTIMEMOD"    TIMESTAMP DEFAULT CURRENT TIMESTAMP,
                                    "USRMOD"      VARCHAR(30),
                                    "PGMMOD"      VARCHAR(30),
                                    "MODCNT"      DECIMAL(10, 0) DEFAULT 0,-- 여기까지가 공통 테이블

                                    "DATACODE"	   DECIMAL(10, 0),
                                    "CTRANSTY"    DECIMAL(10, 0),
                                    "CCLIENT"     VARCHAR(30),
                                    "CORDERID"    VARCHAR(30),
                                    "CORDERTY"    VARCHAR(30),
                                    "CERRID"      DECIMAL(10, 0),
                                    "CTEXT1"      VARCHAR(30),
                                    "CTCODE"      VARCHAR(30),
                                    "CORDERLN"    DECIMAL(10, 0),
                                    "CGAID"       DECIMAL(10, 0),
                                    "CGALWHS"     VARCHAR(30),
                                    "CCOID"       VARCHAR(30),
                                    "CGRWGACT"    DECIMAL(10, 0),
                                    "CREQZONE"    VARCHAR(30),
                                    "CZONE"       VARCHAR(30),
                                    "CLOCID"      VARCHAR(30),
                                    "CERRDSC"     VARCHAR(80),
                                    "CWCID"       VARCHAR(10),
                                    PRIMARY KEY ("LINEID")
);