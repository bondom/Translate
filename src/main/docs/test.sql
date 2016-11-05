CREATE TABLE USER_TEST(ID BIGINT,EMAIL VARCHAR2(225CHAR),PASSWORD VARCHAR2(225CHAR),FIRST_NAME VARCHAR2(15CHAR),
LAST_NAME VARCHAR2(15CHAR),PHONE_NUMBER VARCHAR2(15CHAR), AVATAR BLOB,ROLE VARCHAR2(255CHAR),STATUS VARCHAR2(255CHAR),
BIRTHDAY DATE,COUNTRY VARCHAR2(225CHAR),CITY VARCHAR2(225CHAR),REGISTRATION_DATE TIMESTAMP,PRIMARY KEY(ID));
  Insert into USER_UPDATED_SITE values(1,'test','test',null,'ROLE_USER','test@mail.ru','ACTIVE');

Insert into ADMIN_TEST values(4,'admin@mail.ru','Admin','Adminovych','$2a$10$n405gIRg0biC24NiZ/s9bODmsT2Az39xNRWI7q46siAeW1rmXn/ju','09.08.2016 17:47:21,969000000','ROLE_ADMIN','ACTIVE');
 
Insert into AD_STATUSES_WITH_MESSAGES values(1,'SHOWED','Default message for client SHOWED WRITTEN','Default message for translator SHOWED WRITTEN','WRITTEN');
Insert into AD_STATUSES_WITH_MESSAGES values(2,'ACCEPTED','Default message for client ACCEPTED WRITTEN','Default message for translator ACCEPTED WRITTEN','WRITTEN');
Insert into AD_STATUSES_WITH_MESSAGES values(3,'NOTCHECKED','Default message for client NOTCHECKED WRITTEN','Default message for translator NOTCHECKED WRITTEN','WRITTEN');
Insert into AD_STATUSES_WITH_MESSAGES values(4,'CHECKED','Default message for client CHECKED WRITTEN','Default message for translator CHECKED WRITTEN','WRITTEN');
Insert into AD_STATUSES_WITH_MESSAGES values(5,'REWORKING','Default message for client REWORKING WRITTEN','Default message for translator REWORKING WRITTEN','WRITTEN');
Insert into AD_STATUSES_WITH_MESSAGES values(6,'PAYED','Default message for client PAYED WRITTEN','Default message for translator PAYED WRITTEN','WRITTEN');
Insert into AD_STATUSES_WITH_MESSAGES values(13,'FAILED','Default message for client FAILED','Default message for translator FAILED WRITTEN','WRITTEN');


Insert into AD_STATUSES_WITH_MESSAGES values(7,'SHOWED','Default message for client SHOWED ORAL','Default message for translator SHOWED ORAL','ORAL');
Insert into AD_STATUSES_WITH_MESSAGES values(8,'ACCEPTED','Default message for client ACCEPTED ORAL','Default message for translator ACCEPTED ORAL','ORAL');
Insert into AD_STATUSES_WITH_MESSAGES values(9,'NOTCHECKED','Default message for client NOTCHECKED ORAL','Default message for translator NOTCHECKED ORAL','ORAL');
Insert into AD_STATUSES_WITH_MESSAGES values(10,'CHECKED','Default message for client CHECKED ORAL ','Default message for translator CHECKED ORAL','ORAL');
Insert into AD_STATUSES_WITH_MESSAGES values(11,'REWORKING','Default message for client REWORKING ORAL','Default message for translator REWORKING ORAL','ORAL');
Insert into AD_STATUSES_WITH_MESSAGES values(12,'PAYED','Default message for client PAYED ORAL','Default message for translator PAYED ORAL','ORAL');

Insert into PROJECT_SETTINGS values(1,30,3,3,6,4,3,6,12);
 
 Alter TABLE Ad_TEST DROP CONSTRAINT UK_EJ7V8KBYNO8AN8CUIJA120YC;
 Alter TABLE TRANSLATOR_TEST DROP CONSTRAINT UK_k0lvefre89t5pajk0rh622hwx;
 
 
 Select * from PROJECT_SETTINGS;
 Select * from AD_STATUSES_WITH_MESSAGES;
 Select * from AD_TEST; 
 Select * from WRITTEN_AD; 
 Select * from ORAL_AD; 
 Select * from AD_TEST; 
 Select * from COMMENT_TEST; 
 Select * from CLIENT_TEST;
 Select * from USER_TEST;
 Select * from USER_ENTITY_TEST;
 Select * from ADMIN_TEST;
 Select * from TRANSLATOR_TEST;
 Select * from RESPONDED_AD_TEST;
 Select * from RESULT_DOCUMENT_TEST;
 Select * from INIT_DOCUMENT_TEST;
 Select * from TRANSLATOR_LANGUAGES;
 Select * from ARCHIEVEDAD;

 
 Delete  from CLIENT_TEST where ID = 4;
 Delete from RESPONDED_AD_TEST;
 Delete from AD_TEST;
 Delete from  AD_STATUSES_WITH_MESSAGES;
 
 
 UPDATE TRANSLATOR_TEST SET BALANCE=200 WHERE ID=202;
 
 CREATE TABLE persistent_logins (
    username varchar(64) not null,
    series varchar(64) not null,
    token varchar(64) not null,
    last_used timestamp not null,
    PRIMARY KEY (series)
);
 
 Select * from persistent_logins; 
