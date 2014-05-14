//
//  FBetButtons.h
//  facebet
//
//  Created by Kun on 2013-12-30.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "FScreen.h"

@protocol FBetButtonProtocol <NSObject>

@required

-(void)homeTeamSelected;
-(void)awayTeamSelected;

@end


/**
 The bet buttons in the middle with red & blue backgrounds where users
 can select a team to bet on when viewing upcoming games and just view details
 when viewing past games.
 
 This UI component is reused in many places, FGameController, FBettingVC & FResultVC
 */

@interface FBetButtons : UIView

@property(nonatomic,strong) FGame* myGame;

//@property(strong,nonatomic) UIImageView* greenStripe;
@property(nonatomic,strong) UIView* dimLayer;
@property(nonatomic,strong) UILabel* correctLB;

@property(strong,nonatomic) UIView* greenStripe;
@property(strong,nonatomic) UIImageView* slashGreen;

@property(nonatomic,strong) UIButton* homeButton;
@property(nonatomic,strong) UIButton* awayButton;

@property(nonatomic,strong) UIImageView* homeTeam;
@property(nonatomic,strong) UIImageView* awayTeam;

@property(nonatomic,strong) UIView* homeWhite;
@property(nonatomic,strong) UIView* awayWhite;


@property(nonatomic,strong) UILabel* homeMoney;
@property(nonatomic,strong) UILabel* awayMoney;

@property(nonatomic,strong) UILabel* homeCrowd;
@property(nonatomic,strong) UILabel* awayCrowd;

@property(nonatomic,strong) UIImageView* homePeople;
@property(nonatomic,strong) UIImageView* awayPeople;

@property(nonatomic,strong) UIImageView* meterHome;
@property(nonatomic,strong) UIImageView* meterAway;

@property(nonatomic,strong) UILabel* homeOdds;
@property(nonatomic,strong) UILabel* awayOdds;

@property(nonatomic,strong) UIImageView* homeSmoke;
@property(nonatomic,strong) UIImageView* awaySmoke;

@property(nonatomic,strong) UIButton* overlay;

@property(nonatomic,strong) id<FBetButtonProtocol> delegate;
@property(nonatomic,assign) SEL chooseSel;

-(void)teamSelect:(id)sender;
- (id)initWithFrame:(CGRect)frame;

-(void)setupGame:(FGame*)game past:(BOOL)isPast;

-(void)preSetupHome;
-(void)preSetupAway;


@end
