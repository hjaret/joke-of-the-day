create table JOKE_OF_THE_DAY(
  ID int not null AUTO_INCREMENT,
  JOKE varchar(256) not null,
  DATE DATE,
  DESCRIPTION varchar(200),
  PRIMARY KEY ( ID )
);
create unique index date_idx on JOKE_OF_THE_DAY (DATE);