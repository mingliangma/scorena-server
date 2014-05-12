//
//  MData.m
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "MData.h"

@implementation MData

static MData* sharedInstance = nil;

+(MData*) sharedInstance{
    if(sharedInstance != nil){
        return sharedInstance;
    }
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [MData new];
    });
    return sharedInstance;
}

-(id)getFile{
    NSString* json = [[NSBundle mainBundle] pathForResource:@"Promo" ofType:@"json" inDirectory:nil];
    NSData* data = [NSData dataWithContentsOfFile:json];
    NSError* err;
    return [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments error:&err];
}

-(NSArray*)getArrayWithKey:(NSString*)key{
    id obj = [self getFile];
    NSArray* arr = [obj objectForKey:key];
    NSMutableArray* result = [[NSMutableArray alloc] initWithCapacity:[arr count]];
    int i=0;
    for(i=0;i<[arr count];i++){
        id gameObj = [arr objectAtIndex:i];
        FGame* game = [[FGame alloc] initWithJson:gameObj];
        [result insertObject:game atIndex:i];
    }
    return result;
}

-(FCrowd*)tempCrowd{
    FCrowd * cc = [[FCrowd alloc] init];
    [cc setHomeCrowd:120];
    [cc setAwayCrowd:180];
    [cc setHomeMoney:90];
    [cc setAwayMoney:50];
    return cc;
}

-(NSArray*)getPromoArray{
    return [self getArrayWithKey:@"promo"];
}


-(NSArray*)getGamesArray{
    return [self getArrayWithKey:@"games"];
}

-(NSArray*)getHomePayoutArray{
    id obj = [self getFile];
    return (NSArray*)[obj objectForKey:@"home_payouts"];
}

-(NSArray*)getAwayPayoutArray{
     id obj = [self getFile];
    return (NSArray*)[obj objectForKey:@"away_payouts"];
}


-(NSArray*)getPayoutArray{
    id obj = [self getFile];
    return (NSArray*)[obj objectForKey:@"payouts"];
}


@end
