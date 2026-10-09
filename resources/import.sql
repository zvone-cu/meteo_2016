/* https://www.wunderground.com/history/airport/LDZA/2016/5/19/DailyHistory.html?req_city=Zagreb&req_state=&req_statename=Croatia&reqdb.zip=00000&reqdb.magic=1&reqdb.wmo=14240
*/
/*ovo je automatizirano sve
LOAD DATA LOCAL 
    INFILE 'D:\\new1.txt' INTO TABLE raw_import 
    COLUMNS TERMINATED BY ',' 
    
LOAD DATA LOCAL INFILE 'D:\new1.txt' INTO TABLE meteo_dhmz.raw_import  COLUMNS TERMINATED BY ','     
*/
/*    
 TimeCEST,TemperatureC,Dew PointC,Humidity,Sea Level PressurehPa,VisibilityKm,Wind Direction,Wind SpeedKm/h,Gust SpeedKm/h,Precipitationmm,Events,Conditions,WindDirDegrees,DateUTC
12:00 AM,12.0,3.0,54,1013,-9999.0,SSW,9.3,-,N/A,,Clear,210,2016-05-18 22:00:00
12:30 AM,11.0,4.0,62,1013,-9999.0,SW,5.6,-,N/A,,Clear,230,2016-05-18 22:30:00
1:00 AM,11.0,3.0,58,1012,-9999.0,SSW,11.1,-,N/A,,Clear,210,2016-05-18 23:00:00
1:30 AM,11.0,3.0,58,1012,-9999.0,SW,11.1,-,N/A,,Clear,220,2016-05-18 23:30:00  
*/
/*
select * from raw_import;
delete from raw_import;
drop table raw_import;
*/
    
 /*
 delete from raw_import where timeCEST like '%:30%' 
 delete from raw_import where timeCEST ='12:00 PM'; 
 update raw_import set TimeCest = replace(TimeCest, '12:00 AM', '0') ;
 update raw_import set TimeCest = replace(TimeCest, ':00 AM', '');
 update raw_import set TimeCest = replace(TimeCest, ':00 PM', ' PM');
*/
        
select * from podaci where datum>='2016-05-24 00:00'  order by datum desc
    
select * from podaci where datum>='2016-05-10 00:00' and datum <='2016-05-10 12:00' and grad like 'Zagreb%' order by datum
