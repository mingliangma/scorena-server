//
//  NSDate+GCSameDay.h
//  GoldCare
//
//  Created by Kun on 13-01-17.
//  Copyright (c) 2013 ClearBridge Mobile. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum{
    kAppTimeTypeMissed,
    kAppTimeTypeDone,
    kAppTimeTypeInProgress,
    kAppTimeTypeFuture,
    kAppTimeTypeLocked
}kAppTimeType;


@interface NSDate (GCSameDay)

-(NSString*)countDownFromNow;
-(NSString*)timeString;
-(NSString*)updateFormat;
+(NSDate*)dateFromSQLResult:(NSString*)str;
-(NSInteger)yearValue;
-(NSInteger)monthValue;
-(NSInteger)dayValue;
-(BOOL)isLaterDateThan:(NSDate*)date;
-(BOOL)areTheSameDay:(NSDate*)anotherDate withCalender:(NSCalendar*)cal andComponents:(NSDateComponents*)components;

-(NSDate*)combineWithTime:(NSDate*)timeUnit;

-(NSString*)yearString;
-(NSString*)dateSlash;
-(NSString*)sqlFormat;
-(NSString*)sqlDate;
-(NSString*)sqlTime;
-(NSDate*)fakeDate;

-(NSString*)messageStamp;
-(NSDate*)dateAfterDuration:(NSInteger)duration;

-(NSInteger)duration;
-(NSInteger)durationFromDate:(NSDate*)date;
-(BOOL)isToday;


-(NSString*)yearMonthDate;

-(NSString*)hourMinute;

-(NSString*)AMPM;
-(NSString*)hourMinuteAMPM;

-(NSString*)reallyLongDate;
-(NSString*)monthDateYear;
-(NSString*)monthShort;

-(NSString*)weekdayShort;
-(NSString*)weekdayLong:(BOOL)convToday;
@end
