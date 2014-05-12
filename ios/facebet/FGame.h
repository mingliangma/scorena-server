//
//  FGame.h
//  facebet
//
//  Created by Kun on 2013-12-17.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum{
    FUserPickStatusNone=-1,
    FUserPickStatusTie=0,
    FUserPickStatusWin=1,
    FUserPickStatusLose=2
} FUserPickStatus;

typedef enum{
    FUserPickHome=1,
    FUserPickAway=2,
    FUserPickNone=-1
} FUserPick;

typedef enum{
    FWinnerPickHome=1,
    FWinnerPickAway=2,
    FWinnerPickTie=0,
    FWinnerPickNone=-1
} FWinnerPick;

typedef enum{
    FGameStatusPreEvent,
    FGameStatusPostEvent,
    FGameStatusMidEvent,
    FGameStatusIntermission
} FGameStatus;

@interface FGame : NSObject<NSCopying>

@property(nonatomic,strong) NSString* bet_id;
@property(nonatomic,strong) NSString* game_id;
@property(nonatomic,strong) NSString* gameID;
@property(nonatomic,strong) NSDate* gameTime;
@property(nonatomic,strong) NSString* type;
@property(nonatomic,strong) FTeam* teamA;
@property(nonatomic,strong) FTeam* teamB;
@property(nonatomic,strong) NSArray* types;
@property(nonatomic,strong) NSDictionary* pool;
@property(nonatomic,assign) BOOL placedBet;
@property(nonatomic,strong) NSString* gameStatus;

@property(nonatomic,strong) NSString* pickA;
@property(nonatomic,strong) NSString* pickB;

@property(nonatomic,assign) FUserPick userPick;
@property(nonatomic,assign) FWinnerPick winnerPick;

@property(nonatomic,assign) FUserPickStatus userPickStatus;

-(id)initWithJson:(id)obj;
-(id)initWithFeatureJson:(id)obj;
-(NSString*)gameStatString;

-(void)populateAdditionalData:(id)obj;
-(void)populatePoolData:(id)obj;
-(FGameStatus)getGameStat;

-(NSString*)vsString;
-(UIColor*)vsColor;
+(NSString*)vsStringFromJson:(id)obj;
-(UIColor*)vsColorFromJson:(id)obj;

-(void)updateWithGameDetailJson:(id)obj;
@end
