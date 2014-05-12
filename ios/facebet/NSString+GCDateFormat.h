//
//  NSString+GCDateFormat.h
//  GoldCare
//
//  Created by Kun on 13-01-30.
//  Copyright (c) 2013 ClearBridge Mobile. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <sqlite3.h>

@interface NSString (GCDateFormat)

+(NSString*)numStringForKey:(NSString*)key obj:(id)obj;
-(NSDate*)dateObjectFromNetwork;

-(NSDate*)dateObject;
-(NSDate*)dateTimeObject;


-(NSInteger)durationValue;
-(BOOL)booleanValue;
-(NSDate*)dateValue;
-(NSDate*)timeValue;

-(NSString*)gpsFormatted;

@end
