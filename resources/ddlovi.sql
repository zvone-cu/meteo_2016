
create table podaci (
	id INTEGER AUTO_INCREMENT primary key,
    datum datetime  ,
    termin integer ,
	grad   varchar(50),
	autom  boolean,
	temp   decimal(7,2),
	vlaga  integer,
	tlak   decimal(7,2),
	tlak_tend      varchar(10),
	vjetar_smjer   varchar(3),
	vjetar_brzina  decimal(5,2),
	vrijeme        varchar(50),
	vrijeme_znak   varchar(10)

);
alter table podaci add unique (datum, grad); 

create table selektirani_gradovi 


	grad   varchar( primary key50)


);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-n","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-01","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_01.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-02","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_02.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-03","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_03.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-04","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_04.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-05","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_05.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-06","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_06.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-07","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_07.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-08","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_08.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-09","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_09.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-10","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_10.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-11","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_11.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-12","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_12.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-13","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_13.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-14","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_14.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-15","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_15.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-16","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_16.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-17","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_17.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-18","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_18.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-19","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_19.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-20","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_20.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-21","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_21.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-22","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_22.xml", 80);
insert into runconfig (naziv, target, host ,uri ,port) values ("dhmz-23","dhmz_hrvatska", "vrijeme.hr", "/hrvatska_n_23.xml", 80);



create table prosjeci (
	id INTEGER AUTO_INCREMENT primary key,
	grad   varchar(50),
    mjesec integer  ,
    temp_min decimal (7,2),
    temp_max decimal (7,2),
	temp_sred decimal (7,2),
	padaline integer

);
alter table prosjeci add unique (grad, mjesec); 

insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zagreb-Maksimir", 1,   1.3 ,	4.7	, -2.2 , 38);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zagreb-Maksimir", 2,   2.5, 	7.1, 	-2.0, 50);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zagreb-Maksimir", 3,   7.4, 	12.8, 	2.0  , 60);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zagreb-Maksimir", 4,  12.3, 	18.1, 	6.5 , 66);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zagreb-Maksimir", 5,   16.6, 	22.7, 	10.5  , 86);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zagreb-Maksimir", 6,   20.4,	26.4, 	14.3   , 104);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zagreb-Maksimir", 7,   22.1, 	28.4, 	15.8 , 72);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zagreb-Maksimir", 8,   21.5, 	28.0, 	15.3, 79);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zagreb-Maksimir", 9,   16.9, 	22.4, 	11.4 , 66);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zagreb-Maksimir", 10,   12.2, 	17.0, 	7.4 , 54);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zagreb-Maksimir", 11,   6.8, 	10.4, 	3.3, 118);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zagreb-Maksimir", 12,   2.0, 	5.0, 	-1.0, 53);



/*delete from prosjeci where grad='Zagreb-Maksimir';*/
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zavižan", 1, 	-3.5,  -1.4   ,	-5.6  ,	74);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zavižan", 2,  -3.3,  -1.0,  -5.8,   53);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zavižan", 3,  -3.6 ,  1.1,     -1.3 ,  115);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zavižan", 4,   2.5,   5.2,     0.2 ,   141 );
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zavižan", 5,   8.0,  11.1, 	5.0,     138 );
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zavižan", 6,   12.0, 15.4,    8.7, 98);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zavižan", 7,   14.4, 18.0,   10.8, 70 );
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zavižan", 8,   14.3, 17.8,   10.8, 83);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zavižan", 9,   9.3,   12.1,   6.4, 168 );
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zavižan", 10,  5.7 , 8.0,    3.4, 173);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zavižan", 11,  1.7,  3.0,     -0.2, 201);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Zavižan", 12, -2.3, -0.4,    -4.3  168);


insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Senj", 1, 7.0   ,	9.2  ,	4.8 ,	67);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Senj", 2, 6.9 , 	9.7, 	4.4,    66);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Senj", 3, 10.7, 	13.9 , 	7.5, 	69);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Senj", 4, 14.9, 	18.5, 	11.3, 	72);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Senj", 5, 19.3, 	23.3, 	15.3, 	72);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Senj", 6, 23.6 , 	27.7, 	19.5, 	74);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Senj", 7, 26.0, 	30.2, 	21.7, 	72);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Senj", 8, 25.8, 	30.0 , 	21.7, 	78);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Senj", 9, 21.0, 	24.4, 	17.5, 	82);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Senj", 10, 16.6 , 19.5, 	13.8, 	75);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Senj", 11, 12.5, 	14.8, 	10.2, 	77);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Senj", 12, 8.2, 	10.3, 	6.1, 	78);

insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Hvar", 1, 9.5   ,	12.7  ,	6.8 ,	72);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Hvar", 2, 9.5 , 	13.0, 	6.4,    72);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Hvar", 3, 12.2, 	15.6 , 	8.6, 	74);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Hvar", 4, 15.2, 	18.8, 	11.8, 	77);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Hvar", 5, 20.0, 	23.5, 	15.9, 	75);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Hvar", 6, 24.1 , 	27.9, 	19.7, 	79);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Hvar", 7, 26.7, 	30.8, 	22.4, 	77);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Hvar", 8, 26.0, 	30.7 , 	22.2, 	83);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Hvar", 9, 22.0, 	26.6, 	18.7, 	84);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Hvar", 10, 18.8 , 22.1, 	15.4, 	75);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Hvar", 11, 14.3, 	17.7, 	12.1, 	74);
insert into prosjeci (grad, mjesec, temp_sred ,temp_max ,temp_min, padaline) values ("Hvar", 12, 10.9, 	14.0, 	8.4, 	76);




/* drop table raw_import */
create table raw_import (
    TimeCEST varchar(10),
    temp    decimal(7,2), 
    rosiste  decimal(7,2), 
    vlaga integer,
    tlak  varchar(50),
    vidljivost  varchar(50),
    vjetar_smjer   varchar(3),
    vjetar_brzina  varchar(50),
    udari varchar(50),
    padaline varchar(50),
    Events varchar(50),
    Conditions varchar(50),
    WindDirDegrees varchar(50),
    DateUTC varchar(50),
    id INTEGER AUTO_INCREMENT primary key
    );
    

    
    
    
 /* drop table podaci_anal  */  
 create table podaci_anal (
	id INTEGER AUTO_INCREMENT primary key,
	grad   varchar(50),
    datum  date  ,
    temp_min decimal (7,2),
    temp_max decimal (7,2),
	temp_sred decimal (7,2)


);
alter table podaci_anal add unique (grad, datum); 


 /* drop table korisnici; */
CREATE TABLE korisnici (
  id int(11) NOT NULL,
  username varchar(100) NOT NULL,
  password varchar(100) NOT NULL,
  uspjesno_datum datetime DEFAULT NULL,
  uspjesno int(11) DEFAULT 0,
  PRIMARY KEY (id)
);

INSERT INTO korisnici
(id, username, password, uspjesno_datum, uspjesno)
VALUES(1, 'user1', '123', NULL, 0);

update korisnici set password= sha2("123", 256);



   
    
    
 /* delete from podaci_anal; */
/*
insert into podaci_anal (grad, datum, temp_min, temp_max, temp_sred) 
select grad, date(datum), min(temp) as min1, max(temp) as max1, avg(temp) as avg1 
from podaci 
where datum >='2016-05-01 00:00' and datum<'2016-06-01 00:00'
/*and grad='Zagreb-Maksimir' */
group by grad, date(datum)
order by grad , date(datum)
*/


select * from podaci_anal where grad='Zagreb-Maksimir' order by grad, datum;   
    
    



select * podaci where datum >= '2016-05-13 00:00'
*/
