//
//  FTeam.m
//  facebet
//
//  Created by Kun on 2013-12-20.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FTeam.h"

@implementation FTeam

@synthesize imageName,teamName,score;

-(id)initWithJson:(id)obj{
    self = [super init];
    if(self){

        self.teamName = [obj valueForKey:@"teamname"];
        
        id tmp =[obj objectForKey:@"score"];
        if([tmp isEqual:[NSNull null]]){
            self.score = 0;
        }else{
            self.score = [tmp integerValue];
        }

    }
    return self;
}

+(NSInteger)getScoreWithTeamJson:(id)obj{
    id tmp =[obj objectForKey:@"score"];
    return [tmp isEqual:[NSNull null]]?0:[tmp integerValue];
}

-(id)copyWithZone:(NSZone *)zone{
    FTeam* team = [[FTeam alloc] init];
    [team setTeamName:[teamName copy]];
    [team setScore:score];
    return team;
}

@end
