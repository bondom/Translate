CREATE TABLE USER_TEST(ID BIGINT,EMAIL VARCHAR2(225CHAR),PASSWORD VARCHAR2(225CHAR),FIRST_NAME VARCHAR2(15CHAR),
LAST_NAME VARCHAR2(15CHAR),PHONE_NUMBER VARCHAR2(15CHAR), AVATAR BLOB,ROLE VARCHAR2(255CHAR),STATUS VARCHAR2(255CHAR),
BIRTHDAY DATE,COUNTRY VARCHAR2(225CHAR),CITY VARCHAR2(225CHAR),REGISTRATION_DATE TIMESTAMP,PRIMARY KEY(ID));
  Insert into USER_UPDATED_SITE values(1,'test','test',null,'ROLE_USER','test@mail.ru','ACTIVE');

 Select * from AD_TEST; 
 Select * from COMMENT_TEST; 
 Select * from CLIENT_TEST;
 Select * from USER_TEST;
 Select * from TRANSLATOR_TEST;
 Select * from RESPONSED_AD_TEST;
 Select * from TRANSLATOR_LANGUAGES;
 Delete  from USER_TEST where ID = 152  ;
 Delete from RESPONSED_AD_TEST;
 Delete from AD_TEST;
 
 CREATE TABLE persistent_logins (
    username varchar(64) not null,
    series varchar(64) not null,
    token varchar(64) not null,
    last_used timestamp not null,
    PRIMARY KEY (series)
);
 
 Select * from persistent_logins; 
