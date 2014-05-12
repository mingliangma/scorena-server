//
//  NSDate+GCSameDay.m
//  GoldCare
//
//  Created by Kun on 13-01-17.
//  Copyright (c) 2013 ClearBridge Mobile. All rights reserved.
//

#import "NSDate+GCSameDay.h"

@implementation NSDate (GCSameDay)


-(NSString*)countDownFromNow{
    
    NSInteger interval = [self durationFromDate:[NSDate date]];
    
    NSInteger day = interval/86400;
    NSInteger dayMod = interval%86400;
    NSInteger hour = dayMod/3600;

    NSInteger minMod = dayMod%3600;
    NSInteger minute = minMod/60;
    
    NSInteger second = minMod%60;
    
    if(day==0){
        return [NSString stringWithFormat:@"%02d:%02d:%02d",hour,minute,second];
    }else if(day==1){
        return [NSString stringWithFormat:@"1 Day: %02d:%02d:%02d",hour,minute,second];
    }else{
        return [NSString stringWithFormat:@"%d Days: %02d:%02d:%02d",day,hour,minute,second];
    }
}

+(NSDate*)dateFromSQLResult:(NSString*)str{
    
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd"];
    NSDate* __weak date = [formatter dateFromString:str];
    return date;
}

-(NSString*)timeString{
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"HH:mm a"];
    return     [formatter stringFromDate:self];
}

-(NSString*)yearString{
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *later = [cal components:NSYearCalendarUnit fromDate:self];
    return [NSString stringWithFormat:@"%d",[later year]];
}

-(NSInteger)yearValue{
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *later = [cal components:NSYearCalendarUnit fromDate:self];
    return [later year];    
}

-(NSInteger)monthValue{
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *later = [cal components:NSMonthCalendarUnit fromDate:self];
    return [later month];
}

-(NSInteger)dayValue{
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *later = [cal components:NSDayCalendarUnit fromDate:self];
    return [later day];
}

-(BOOL)isLaterDateThan:(NSDate*)date{
    
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *later = [cal components:(NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit) fromDate:self];
    long laterNum = [later year]*10000 +[later month]*100+[later day];
    
    NSDateComponents* earlier = [cal components:(NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit) fromDate:date];
    long earlierNum = [earlier year]*10000 + [earlier month]*100 + [earlier day];

    return laterNum>earlierNum;
}



-(BOOL)areTheSameDay:(NSDate*)anotherDate withCalender:(NSCalendar*)cal andComponents:(NSDateComponents*)components{
    
    if(!anotherDate)
        return NO;
    
    components = [cal components:(NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit) fromDate:self];
    NSInteger dayUnit = [components day];
    NSInteger monthUnit = [components month];
    NSInteger yearUnit = [components year];
    
    NSDateComponents* newComponent = [cal components:(NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit) fromDate:anotherDate];
    NSInteger anotherDay = [newComponent day];
    NSInteger anotherMonth = [newComponent month];
    NSInteger anotherYear = [newComponent year];
    
    return (yearUnit==anotherYear)&&(monthUnit==anotherMonth)&&(dayUnit==anotherDay);
}

/**
 *  Replace the time components of self with the time units of the new date
 */

-(NSDate*)combineWithTime:(NSDate*)timeUnit{
    
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *dateComp;
    dateComp = [cal components:(NSYearCalendarUnit|NSMonthCalendarUnit|NSDayCalendarUnit) fromDate:self];
    
    NSDateComponents *timeComp;
    timeComp = [cal components:(NSHourCalendarUnit|NSMinuteCalendarUnit|NSSecondCalendarUnit) fromDate:timeUnit];
        
    NSInteger hourUnit = [timeComp hour];
    NSInteger minuteUnit = [timeComp minute];
    NSInteger secondUnit = [timeComp second];
    
    [dateComp setHour:hourUnit];
    [dateComp setMinute:minuteUnit];
    [dateComp setSecond:secondUnit];
        
    return [cal dateFromComponents:dateComp];
}

-(NSString*)sqlFormat{
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    return [formatter stringFromDate:self];
}

-(NSString*)updateFormat{
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    return [formatter stringFromDate:self];
}

-(NSString*)sqlDate{
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd"];
    return [formatter stringFromDate:self];
}
-(NSString*)sqlTime{
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"hh:mm:ss"];
    return [formatter stringFromDate:self];
}



-(NSDate*)fakeDate{    
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *components = [cal components:(NSHourCalendarUnit|NSMinuteCalendarUnit|NSYearCalendarUnit|NSMonthCalendarUnit|NSHourCalendarUnit) fromDate:[NSDate date]];
    [components setHour:17];
    [components setMinute:10];
    NSDate* date = [cal dateFromComponents:components];
    return date;
}




//EMP #192 Nov. 04, 2012 @5:37PM
-(NSString*)messageStamp{
    NSString* str = [NSString stringWithFormat:@"%@ @ %@%@",[self monthDateYear],[self hourMinute],[self AMPM]];
    return str;
}

-(NSDate*)dateAfterDuration:(NSInteger)duration{    
    return [self dateByAddingTimeInterval:duration];
}

-(NSInteger)duration{
        
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *components = [cal components:(NSHourCalendarUnit|NSMinuteCalendarUnit) fromDate:self];
    NSUInteger duration = [components hour]*60+[components minute];

//    DD(@"%d hr, %d min",[components hour],[components minute]);
    return duration;
}

-(NSInteger)durationFromDate:(NSDate*)date{
    
    NSTimeInterval elapsed = [self timeIntervalSinceDate:date];

    return(NSInteger)elapsed;
}

/**
 *
 */

-(BOOL)isToday{
    NSDate* today = [NSDate date];
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *components;
    
    return [self areTheSameDay:today withCalender:cal andComponents:components];
}

/**
 *  Date display category methods
 */

-(NSString*)yearMonthDate{
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    
    NSDateComponents* components = [cal components:(NSYearCalendarUnit|NSDayCalendarUnit) fromDate:self];
        
    NSString* str = [NSString stringWithFormat:@"%ld %@ %ld",(long)[components year],[self monthShort],(long)[components day]];
    return str;
}


-(NSString*)hourMinute{
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *components = [cal components:(NSHourCalendarUnit|NSMinuteCalendarUnit) fromDate:self];
    
    NSInteger hr = [components hour]%12;
    return [NSString stringWithFormat:@"%.2d:%.2ld",hr==0?12:hr,(long)[components minute]];
}

-(NSString*)AMPM{
    
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *components = [cal components:(NSHourCalendarUnit|NSMinuteCalendarUnit) fromDate:self];
    
    if([components hour]>=12){
        return @"PM";
    }else{
        return @"AM";
    }    
}

-(NSString*)hourMinuteAMPM{
    return [NSString stringWithFormat:@"%@ %@",[self hourMinute],[self AMPM]];
}


-(NSString*)reallyLongDate{
    
    NSString* str;
    str = [NSString stringWithFormat:@"%@ %@ %@, %@",[self hourMinute],[self AMPM],[self weekdayShort],[self yearMonthDate]];
    return str;
}

-(NSString*)monthDateYear{
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *components = [cal components:(NSDayCalendarUnit|NSYearCalendarUnit) fromDate:self];
    
    return [NSString stringWithFormat:@"%@ %.2d, %d",[self monthShort],[components day],[components year]];
}

-(NSString*)dateSlash{
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *components = [cal components:(NSDayCalendarUnit|NSMonthCalendarUnit) fromDate:self];
    
    return [NSString stringWithFormat:@"%.2ld/%.2ld",(long)[components month],(long)[components day]];
}

-(NSString*)monthShort{
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *components = [cal components:(NSMonthCalendarUnit) fromDate:self];
    NSString* str;
    switch ([components month]) {
        case 1:
            str = @"Jan";
            break;
        case 2:
            str = @"Feb";
            break;
        case 3:
            str = @"Mar";
            break;
        case 4:
            str = @"Apr";
            break;
        case 5:
            str = @"May";
            break;
        case 6:
            str = @"Jun";
            break;
        case 7:
            str = @"Jul";
            break;
        case 8:
            str = @"Aug";
            break;
        case 9:
            str = @"Sep";
            break;
        case 10:
            str = @"Oct";
            break;
        case 11:
            str = @"Nov";
            break;
        case 12:
            str = @"Dec";
            break;            
        default:
            break;
    }
    return str;
}

-(NSString*)weekdayShort{
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *components = [cal components:(NSWeekdayCalendarUnit) fromDate:self];
    NSString* str;
    
    switch ([components weekday]) {
        case 1:
            str = @"Sun";
            break;
        case 2:
            str = @"Mon";
            break;
        case 3:
            str = @"Tue";
            break;
        case 4:
            str = @"Wed";
            break;
        case 5:
            str = @"Thu";
            break;
        case 6:
            str = @"Fri";
            break;
        case 7:
            str = @"Sat";
            break;
        default:
            break;
    }
    return str;
}

-(NSString*)weekdayLong:(BOOL)convToday{
    NSCalendar* cal = [[NSCalendar alloc] initWithCalendarIdentifier:NSGregorianCalendar];
    NSDateComponents *components = [cal components:(NSWeekdayCalendarUnit) fromDate:self];
    NSString* str;
    
    if(convToday){
        NSDateComponents* tmpComp;
        if([self areTheSameDay:[NSDate date] withCalender:cal andComponents:tmpComp]){
            return @"Today";
        }
    }
    switch ([components weekday]) {
        case 1:
            str = @"Sunday";
            break;
        case 2:
            str = @"Monday";
            break;
        case 3:
            str = @"Tuesday";
            break;
        case 4:
            str = @"Wednesday";
            break;
        case 5:
            str = @"Thursday";
            break;
        case 6:
            str = @"Friday";
            break;
        case 7:
            str = @"Saturday";
            break;
        default:
            break;
    }
    return str;
}

@end
