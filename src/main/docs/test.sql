CREATE TABLE USER_TEST(ID BIGINT,EMAIL VARCHAR2(225CHAR),PASSWORD VARCHAR2(225CHAR),FIRST_NAME VARCHAR2(15CHAR),
LAST_NAME VARCHAR2(15CHAR),PHONE_NUMBER VARCHAR2(15CHAR), AVATAR BLOB,ROLE VARCHAR2(255CHAR),STATUS VARCHAR2(255CHAR),
BIRTHDAY DATE,COUNTRY VARCHAR2(225CHAR),CITY VARCHAR2(225CHAR),REGISTRATION_DATE TIMESTAMP,PRIMARY KEY(ID));
  Insert into USER_UPDATED_SITE values(1,'test','test',null,'ROLE_USER','test@mail.ru','ACTIVE');

Insert into USER_TEST values(3554,null,'20.04.1111','Rio',null,'COUNTRY','admin@mail.ru','CONFIRMED','Admin','Adminovych','$2a$10$n405gIRg0biC24NiZ/s9bODmsT2Az39xNRWI7q46siAeW1rmXn/ju','+380666112256','09.08.2016 17:47:21,969000000','ROLE_ADMIN','ACTIVE');
 Select * from AD_TEST; 
 Select * from COMMENT_TEST; 
 Select * from CLIENT_TEST;
 Select * from USER_TEST;
 Select * from TRANSLATOR_TEST;
 Select * from RESPONSED_AD_TEST;
 Select * from TRANSLATOR_LANGUAGES;
 Delete  from USER_TEST where ID = 3554;
 Delete from RESPONSED_AD_TEST;
 Delete from AD_TEST;
 UPDATE USER_TEST SET STATUS='ACTIVE' WHERE ID=1;
 
 CREATE TABLE persistent_logins (
    username varchar(64) not null,
    series varchar(64) not null,
    token varchar(64) not null,
    last_used timestamp not null,
    PRIMARY KEY (series)
);
 
 Select * from persistent_logins; 
