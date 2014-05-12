//
//  FGlobal.m
//  facebet
//
//  Created by Kyle on 2014-01-03.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FGlobal.h"

@implementation FGlobal

@synthesize current,sessionToken,balance;

@synthesize userId,userName,region,email,gender,credential;

static FGlobal* sharedInstance = nil;


+(FGlobal*) sharedInstance{
    if(sharedInstance != nil){
        return sharedInstance;
    }
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [FGlobal new];
    });
    return sharedInstance;
}

-(BOOL)authenticated{
    
    if([self credential]){
        return YES;
    }
    return NO;
}

-(void)logout{
    [self setCredential:NULL];
    [[NSNotificationCenter defaultCenter] postNotificationName:kRefreshBalanceNotification object:nil];
}

-(void)reduceBalance:(NSInteger)bal{
    
    NSInteger oldBalance = [self balance];
    
    NSInteger newBalance = oldBalance - bal;
    [self setBalance:newBalance];
}


-(NSInteger)balance{
    if([self authenticated]){
        return [[[self defaultCredentials] objectForKey:FBalanceKey] integerValue];
    }else{
        return 0;
    }
}

-(void)setBalance:(NSInteger)myBalance{
    NSUserDefaults* defaults = [NSUserDefaults standardUserDefaults];
    NSMutableDictionary* dict = [NSMutableDictionary dictionaryWithDictionary:[defaults objectForKey:FCredential]];
    [dict setObject:[NSNumber numberWithInteger:myBalance] forKey:FBalanceKey];
    [defaults setObject:dict forKey:FCredential];
    [defaults synchronize];

    [[NSNotificationCenter defaultCenter] postNotificationName:kRefreshBalanceNotification object:nil];
}

-(id)credential{
    NSUserDefaults* defaults = [NSUserDefaults standardUserDefaults];
    credential = [defaults objectForKey:FCredential];
    return credential;
}

-(void)setCredential:(id)myCredential{
    credential = myCredential;
    NSUserDefaults* defaults = [NSUserDefaults standardUserDefaults];
    if(credential){
        [defaults setObject:[credential copy] forKey:FCredential];
    }else{
        [defaults removeObjectForKey:FCredential];
    }
    [defaults synchronize];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:kRefreshBalanceNotification object:nil];
}

-(id)defaultCredentials{
    return [self credential];
}

-(NSString*)sessionToken{
    return [[self defaultCredentials] objectForKey:@"sessionToken"];
}

-(NSString*)userName{
    return [[self defaultCredentials] objectForKey:@"username"];
}

-(NSString*)email{
    return [[self defaultCredentials] objectForKey:@"email"];
}

-(NSString*)region{
    return [[self defaultCredentials] objectForKey:@"region"];
}

-(NSString*)gender{
    return [[self defaultCredentials] objectForKey:@"gender"];
}

-(NSString*)userId{
    return [[self defaultCredentials] objectForKey:@"userId"];
}

@end
