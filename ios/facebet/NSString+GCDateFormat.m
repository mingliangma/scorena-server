//
//  NSString+GCDateFormat.m
//  GoldCare
//
//  Created by Kun on 13-01-30.
//  Copyright (c) 2013 ClearBridge Mobile. All rights reserved.
//

#import "NSString+GCDateFormat.h"

@implementation NSString (GCDateFormat)


+(NSString*)numStringForKey:(NSString*)key obj:(id)obj{
    
    id tmp = [obj objectForKey:key];
    if([tmp isEqual:[NSNull null]]){
        return @"0";
    }else{
        return [NSString stringWithFormat:@"%d",[tmp integerValue]];
    }
}

-(NSString*)gpsFormatted{
    return [self stringByReplacingOccurrencesOfString:@" " withString:@"+"];
}

-(NSDate*)dateObjectFromNetwork{
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'"];
    return [formatter dateFromString:self];
}


-(NSDate*)dateObject{
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"MM-dd-yyyy"];
    return [formatter dateFromString:self];
}

-(NSDate*)dateTimeObject{
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
 //   NSString* tt = [[self stringByReplacingOccurrencesOfString:@"" withString:@" "] stringByReplacingOccurrencesOfString:@"Z" withString:@""];
  //  NSLog(@"tt%@",tt);
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss.s zzz"];
  //  [formatter setDateFormat:@"dd MMM yyyy HH:mm:ss zzz"];
    //Change to local time
    formatter.timeZone = [NSTimeZone systemTimeZone];
    return [formatter dateFromString:self];
}

/* Orginal Code
-(NSDate*)dateTimeObject_old{
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    NSString* tt = [[self stringByReplacingOccurrencesOfString:@"T" withString:@" "] stringByReplacingOccurrencesOfString:@"Z" withString:@""];
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    //Change to local time
    formatter.timeZone = [NSTimeZone systemTimeZone];
    return [formatter dateFromString:tt];
}*/




-(NSInteger)durationValue{
    if([self length]!=8)
        return 0;
    
    NSInteger hour = [[self substringToIndex:2] integerValue];
    NSInteger minute = [[self substringWithRange:NSMakeRange(3, 2)] integerValue];
    NSInteger second = [[self substringFromIndex:6] integerValue];
    return hour*3600+minute*60+second;
}

-(BOOL)booleanValue{
    return [self isEqualToString:@"Y"]?YES:NO;
}

-(NSDate*)dateValue{
    if([self length]!=10)
        return nil;
    
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd"];
    NSDate* result = [formatter dateFromString:[self copy]];
    return result;
}

-(NSDate*)timeValue{
    if([self length]!=8)
        return nil;
    
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSDate* result = [formatter dateFromString:[NSString stringWithFormat:@"1970-01-01 %@",[self copy]]];
    return result;
}


@end
