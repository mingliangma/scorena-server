//
//  FWager.m
//  facebet
//
//  Created by Kun on 2013-12-28.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FWager.h"

@implementation FWager

@synthesize amount,payout,name;

-(id)initWithJson:(id)obj{
    self = [super init];
    if(self){
        self.name = [obj objectForKey:@"name"];
        self.amount = (NSInteger)[[obj objectForKey:@"wager"] integerValue];
        self.payout = (NSInteger)[[obj objectForKey:@"expectedWinning"] integerValue];
    }
    return self;
}

@end
