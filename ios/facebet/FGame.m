//
//  FGame.m
//  facebet
//
//  Created by Kun on 2013-12-17.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FGame.h"

@implementation FGame

@synthesize gameID,gameTime,type,teamA,teamB,types,game_id,pool,bet_id;
@synthesize placedBet,gameStatus,userPick,winnerPick,userPickStatus,pickA,pickB;


-(id)copyWithZone:(NSZone *)zone{
    FGame* result = [[FGame alloc] init];
    
    [result setGame_id:[game_id copy]];
    [result setPool:[pool copy]];
    [result setType:[type copy]];
    [result setGameTime:[gameTime copy]];
    [result setTeamA:[teamA copy]];
    [result setTeamB:[teamB copy]];
    [result setBet_id:[bet_id copy]];
    [result setPlacedBet:placedBet];
    [result setUserPick:userPick];
    [result setGameStatus:[gameStatus copy]];
    [result setWinnerPick:winnerPick];
    [result setUserPickStatus:userPickStatus];
    [result setPickA:pickA];
    [result setPickB:pickB];
    return result;
}

-(void)populateAdditionalData:(id)obj{
    
    [self setPickA:[obj objectForKey:@"pick1"]];
    [self setPickB:[obj objectForKey:@"pick2"]];
    [self setPool:[obj objectForKey:@"pool"]];
    [self setBet_id:[obj objectForKey:@"questionId"]];
    [self setPlacedBet:[[[obj objectForKey:@"userInfo"] objectForKey:@"placedBet"] boolValue]];
    [self setUserPick:[[[obj objectForKey:@"userInfo"] objectForKey:@"userPick"] integerValue]];
    [self setWinnerPick:[[obj objectForKey:@"winnerPick"] integerValue]];
    [self setUserPickStatus:[[[obj objectForKey:@"userInfo"] objectForKey:@"userPickStatus"] integerValue]];
}

-(void)populatePoolData:(id)obj{
    
    id pp = [obj objectForKey:@"pool"];
    
    if(![pp objectForKey:@"pick1odds"]){
        
        if(pool){
            
            NSMutableDictionary* dd = [[NSMutableDictionary alloc] initWithDictionary:pool];
            
            [dd setObject:[pp objectForKey:@"currentOddsPick1"] forKey:@"currentOddsPick1"];
            [dd setObject:[pp objectForKey:@"currentOddsPick2"] forKey:@"currentOddsPick2"];
            
            [self setPool:dd];
        }
    }else{
        [self setPool:[obj objectForKey:@"pool"]];
    }
    
}

/*
 
 {
 away =         {
 score = "<null>";
 teamname = "Granada ";
 };
 date = "2014-05-05T20:00:00Z";
 gameId = "l.lfp.es.primera-2013-e.1803801";
 gameStatus = "pre-event";
 home =         {
 score = "<null>";
 teamname = "Real Sociedad ";
 };
 leagueCode = "l.lfp.es.primera";
 leagueName = "La Liga";
 question =         {
 content = "Who will win";
 pick1 = "Real Sociedad";
 pick2 = Granada;
 pool =             {
 pick1Amount = 55;
 pick1NumPeople = 3;
 pick1PayoutPercent = 35;
 pick1odds = "2.82";
 pick2Amount = 100;
 pick2NumPeople = 6;
 pick2PayoutPercent = 65;
 pick2odds = "1.55";
 };
 questionId = 40;
 userInfo =             {
 placedBet = 0;
 userPick = "-1";
 userPickStatus = "-1";
 winnerPick = "-1";
 };
 };
 type = soccer;
 }
 
 */

-(id)initWithFeatureJson:(id)obj{
//    sfv(obj)
    self = [self initWithJson:obj];
    [self setPool:[[obj objectForKey:@"question"] objectForKey:@"pool"]];
    [self setType:[[obj objectForKey:@"question"] objectForKey:@"content"]];
    [self setBet_id:[[obj objectForKey:@"question"] objectForKey:@"questionId"]];
    [self setPickA:[[obj objectForKey:@"question"] objectForKey:@"pick1"]];
    [self setPickB:[[obj objectForKey:@"question"] objectForKey:@"pick2"]];
    return self;
}

-(NSString*)description{
    NSMutableString* str = [[NSMutableString alloc] initWithFormat:@"\nPlaced bet: %@\n",placedBet?@"YES":@"NO"];
    [str appendFormat:@"userPickStatus: %d\n",userPickStatus];
    [str appendFormat:@"userPick: %d\n",userPick];
    [str appendFormat:@"winnerPick: %d\n",winnerPick];
    [str appendFormat:@"gameStatus: %@\n",gameStatus];
    [str appendFormat:@"gameId:%@\n",gameID];
    [str appendFormat:@"QuestionId: %@",bet_id];
    return str;
}

-(FGameStatus)getGameStat{
    if([gameStatus isEqualToString:@"intermission"]){
        return FGameStatusIntermission;
    }else if([gameStatus isEqualToString:@"mid-event"]){
        return FGameStatusMidEvent;
    }else if([gameStatus isEqualToString:@"pre-event"]){
        return FGameStatusPreEvent;
    }else{
        return FGameStatusPostEvent;
    }
}

-(NSString*)vsString{
    if([self getGameStat] == FGameStatusPreEvent){
        return @" v.s";
    }else{
        return [NSString stringWithFormat:@"%d:%d",teamA.score,teamB.score];
    }
}

+(NSString*)vsStringFromJson:(id)obj{
    if([[obj objectForKey:@"gameStatus"] isEqualToString:@"pre-event"]){
        return @" v.s";
    }else{
        id home = [obj objectForKey:@"home"];
        id away = [obj objectForKey:@"away"];
        
        NSInteger homeScore = [FTeam getScoreWithTeamJson:home];
        NSInteger awayScore = [FTeam getScoreWithTeamJson:away];
        return [NSString stringWithFormat:@"%d:%d",homeScore,awayScore];
    }    
}
-(UIColor*)vsColorFromJson:(id)obj{
    if([[obj objectForKey:@"gameStatus"] isEqualToString:@"pre-event"]){
        return [UIColor fGreenColor];
    }else{
        return [UIColor fRedColor];
    }
}

-(UIColor*)vsColor{
    return [self getGameStat]==FGameStatusPreEvent?[UIColor fGreenColor]:[UIColor fRedColor];
}

-(NSString*)gameStatString{
    
    if([self getGameStat] == FGameStatusMidEvent){
        return @"Playing";
    }else if([self getGameStat] == FGameStatusIntermission){
        return @"Intermission";
    }else if([self getGameStat] == FGameStatusPreEvent){
        return [[self gameTime] timeString];
    }else if([self getGameStat] == FGameStatusPostEvent){
        return @"End";
    }
    return @"";
}

-(id)initWithJson:(id)obj{
    self = [super init];
    if(self){
        
        id tmpA = [obj objectForKey:@"home"];
        id tmpB = [obj objectForKey:@"away"];
        
        FTeam* a = [[FTeam alloc] initWithJson:tmpA];
        FTeam* b = [[FTeam alloc] initWithJson:tmpB];
        
        [self setTeamA:a];
        [self setTeamB:b];
        
        NSString* str = [obj objectForKey:@"date"];
        //First attempt to convert the date
        NSDateFormatter *fmt = [[NSDateFormatter alloc] init];
       fmt.dateFormat = @"yyyy-MM-dd HH:mm:ss";
        NSDate *utc = [str dateObject];
        fmt.timeZone = [NSTimeZone systemTimeZone];
        NSString *local = [fmt stringFromDate:utc];
        NSLog(@"local: %@", local);
         NSLog(@"str: %@", str);
        
     //   [self setGameTime:[local dateTimeObject]];
        
         [self setGameTime:[str dateTimeObject]];
        [self setGame_id:[obj objectForKey:@"gameId"]];
        [self setPlacedBet:[[obj objectForKey:@"placedBet"] boolValue]];
        [self setUserPickStatus:[[[obj objectForKey:@"userInfo"] objectForKey:@"userPickStatus"] integerValue]];
        [self setUserPick:[[[obj objectForKey:@"userInfo"] objectForKey:@"userPick"] integerValue]];
        [self setWinnerPick:[[obj objectForKey:@"winnerPick"] integerValue]];
        [self setGameStatus:[obj objectForKey:@"gameStatus"]];
    }
    return self;
}

-(void)updateWithGameDetailJson:(id)obj{
    id tmpA = [obj objectForKey:@"home"];
    id tmpB = [obj objectForKey:@"away"];
    
    FTeam* a = [[FTeam alloc] initWithJson:tmpA];
    FTeam* b = [[FTeam alloc] initWithJson:tmpB];
    
    [self setTeamA:a];
    [self setTeamB:b];
    
    NSString* str = [obj objectForKey:@"date"];
    [self setGameTime:[str dateTimeObject]];
    [self setGame_id:[obj objectForKey:@"gameId"]];
    [self setGameStatus:[obj objectForKey:@"gameStatus"]];
}

@end
