//
//  NSDictionary+Signup.m
//  facebet
//
//  Created by Kyle on 2014-04-20.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "NSDictionary+Signup.h"

@implementation NSDictionary (Signup)

-(NSString*)singupString{
    NSMutableString* str = [[NSMutableString alloc] initWithString:@"{"];
    
    [str appendFormat:@"\"username\":\"%@\",",[self objectForKey:SU_UserKey]];
    [str appendFormat:@"\"email\":\"%@\",",[self objectForKey:SU_EmailKey]];
    [str appendFormat:@"\"password\":\"%@\",",[self objectForKey:SU_PassKey]];
    [str appendFormat:@"\"gender\":\"%@\",",[self objectForKey:SU_GenderKey]];
    [str appendFormat:@"\"region\":\"%@\"}",[self objectForKey:SU_RegionKey]];
    return str;
}

-(NSString*)betString{
    NSMutableString* str = [[NSMutableString alloc] initWithString:@"{"];
    [str appendFormat:@"\"sessionToken\":\"%@\",",[[FGlobal sharedInstance] sessionToken]];
    [str appendFormat:@"\"questionId\":\"%@\",",[self objectForKey:SU_QuestionKey]];
    [str appendFormat:@"\"pick\":\"%@\",",[self objectForKey:SU_PickKey]];
    [str appendFormat:@"\"wager\":\"%@\"}",[self objectForKey:SU_WagerKey]];
    return str;
}

@end
