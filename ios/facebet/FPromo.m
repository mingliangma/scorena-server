//
//  FPromo.m
//  facebet
//
//  Created by Kun on 2013-12-17.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FPromo.h"

@implementation FPromo

@synthesize gameTime,type,teamA,teamB;


-(id)initWithJson:(id)obj{
    self = [super init];
    if(self){
        
        id tmpA = [obj objectForKey:@"teamA"];
        id tmpB = [obj objectForKey:@"teamB"];
        
        FTeam* a = [[FTeam alloc] initWithJson:tmpA];
        FTeam* b = [[FTeam alloc] initWithJson:tmpB];
        
        [self setTeamA:a];
        [self setTeamB:b];
        
        NSString* str = [obj objectForKey:@"date"];
        [self setGameTime:[str dateObject]];
        
        [self setType:[obj objectForKey:@"type"]];
        
    }
    return self;
}


@end

