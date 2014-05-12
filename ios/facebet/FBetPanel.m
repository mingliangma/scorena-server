//
//  FBetPanel.m
//  facebet
//
//  Created by Kun on 2013-12-30.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FBetPanel.h"

@implementation FBetPanel

@synthesize wagerScroll,expect1,expectLB,minimumLB,oddsBlue,gameObj;

@synthesize lastUpdate,strips,wager,pickPanel;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        [self loadBetScroll];
        [self loadPayouts];
        wager = 15;
        
    }
    return self;
}

#pragma mark - Public Methods

-(void)populateWithJson:(id)obj past:(BOOL)alreadyBet team:(kBetSelectType)betSelectType bet:(FGame*)betObj{
    gameObj = obj;
    didBet = alreadyBet;
    betType = betSelectType;
    
    //Write the middle green odds string
    [self writeMiddleOdds];
    
    //Get the number for two pools
    poolA = [[[gameObj objectForKey:@"pool"] objectForKey:@"pick1Amount"] integerValue];
    poolB = [[[gameObj objectForKey:@"pool"] objectForKey:@"pick2Amount"] integerValue];
    
    //If user has already bet on panel, then just display the wager,
    if(alreadyBet){
        wager = [[[gameObj objectForKey:@"userInfo"] objectForKey:@"userWager"] integerValue];
        FUserPick userPick = [[[obj objectForKey:@"userInfo"] objectForKey:@"userPick"] integerValue];
        if(userPick == FUserPickHome){
            [self homeSelected];
        }else if(userPick == FUserPickAway){
            [self awaySelected];
        }else{
            
        }
        [wagerScroll selectAlreadyPlacedWager:wager];
        [self setupGrayUIWhenBetPlaced];
    }else{
        
        if([betObj getGameStat] == FGameStatusPreEvent){
            

            if(betSelectType==kBetSelectHome){
                [self homeSelected];
            }else if(betSelectType == kBetSelectAway){
                [self awaySelected];
            }else{
                DD(@"ERROR: No Bets Selected For Game???");
            }
            [wagerScroll selectNewBetWager:wager];
            
        }else{
            [wagerScroll selectAlreadyPlacedWager:0];
            [self setupGrayUIWhenBetPlaced];
        }
    }
}

-(void)setupGrayUIWhenBetPlaced{
    [pickPanel setupGrayUIWhenBetPlaced];   
}

-(void)configureUpdateTime:(NSString*)str{
    [lastUpdate setText:[NSString stringWithFormat:@"Last Updated: %@",str]];
}

-(void)newWagerSelected:(NSNumber *)num{
    wager = [num integerValue];
    
    if(betType==kBetSelectHome){
        [self homeSelected];
    }else{
        [self awaySelected];
    }
}



-(void)homeSelected{

    betType = kBetSelectHome;
    [pickPanel configureWithPick:[self.gameObj objectForKey:@"pick1"]];
    
    CGFloat net,total,ratio;
    
    if(didBet){
        net = (wager*1.0/poolA)*poolB;
    }else{
        net = (wager*1.0/(poolA+wager))*poolB;
    }
    
    total = net+wager;
    ratio = net/wager;
    [self configureExpectedPayout:total ratio:ratio];
}

-(void)awaySelected{

    betType = kBetSelectAway;
    [pickPanel configureWithPick:[self.gameObj objectForKey:@"pick2"]];
    
    CGFloat net,total,ratio;
    
    if(didBet){
        net = (wager*1.0/poolB)*poolA;
    }else{
        net = (wager*1.0/(poolB+wager))*poolA;
    }
    
    total = net+wager;
    ratio = net/wager;
    [self configureExpectedPayout:total ratio:ratio];
}

#pragma mark - Private Methods

-(void)configureExpectedPayout:(CGFloat)payout ratio:(CGFloat)ratio{
    if(isnan(payout)){
        payout = 0;
    }
    
    if(isnan(ratio)){
        ratio = 0;
    }
    [expect1 setText:[NSString stringWithFormat:@"$%.0f, %.0f%%",payout,ratio*100+100]];
}

-(void)writeMiddleOdds{
    
    CGFloat odd1 = [[[gameObj objectForKey:@"pool"] objectForKey:@"currentOddsPick1"] floatValue];
    CGFloat odd2 = [[[gameObj objectForKey:@"pool"] objectForKey:@"currentOddsPick2"] floatValue];
    NSString* resultOdds =[NSString stringWithFormat:@"%.02f:%.02f",odd2,odd1];
    NSString* result = [NSString stringWithFormat:@"%@/%@ = %@",[gameObj objectForKey:@"pick1"],[gameObj objectForKey:@"pick2"],resultOdds];
    [minimumLB setText:result];
}

#pragma mark - UI Initializations

-(void)loadPayouts{
    strips = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"strips.png"]];
   // strips = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Scorena_App_Stripe.jpg"]];
    
    expectLB = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 160, 26)];
    [expectLB setTextColor:[UIColor fGreenColor]];
    [expectLB setText:@"EXPECTED PAYOUT:"];
    [expectLB setFont:[UIFont fStraightFont:12]];
    
    minimumLB = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 280, 26)];
    [minimumLB setTextColor:[UIColor fGreenColor]];
    [minimumLB setText:@"Chelsea / Arsenal: 4:1"];
    [minimumLB setTextAlignment:NSTextAlignmentCenter];
    [minimumLB setFont:[UIFont fStraightFont:12]];

    
    expect1 = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 80, 30)];
    [expect1 setTextColor:[UIColor fRedColor]];
    [expect1 setFont:[UIFont fStraightFont:13]];

    oddsBlue = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 140, 30)];
    [oddsBlue setTextColor:[UIColor fBlueColor]];
    [oddsBlue setFont:[UIFont boldSystemFontOfSize:10]];
    [oddsBlue setTextAlignment:NSTextAlignmentCenter];
    [oddsBlue setText:@"CURRENT ODDS"];
    
    lastUpdate = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 180, 30)];
    [lastUpdate setTextColor:[UIColor fDarkGrayColor]];
    [lastUpdate setTextAlignment:NSTextAlignmentCenter];
    [lastUpdate setFont:[UIFont systemFontOfSize:9]];
    [lastUpdate setText:@"Last Updated: "];
    
    
    [strips shift:15 y:100];
    [expectLB shift:60 y:150];
    [minimumLB shift:15 y:110];
    [expect1 shift:175 y:148];
    [oddsBlue shift:90 y:95];
    [lastUpdate shift:70 y:128];
    
    [self addSubview:strips];
    [self addSubview:oddsBlue];
    [self addSubview:lastUpdate];
    [self addSubview:expect1];    
    [self addSubview:expectLB];
    [self addSubview:minimumLB];
}


-(void)loadBetScroll{
    
    pickPanel = [[FPickPanel alloc] initWithPoint:CGPointMake(0, 0)];
    [self addSubview:pickPanel];
    
     wagerScroll = [[FWagerScroll alloc] initWithFrame:CGRectMake(20, 53, 260, 48)];
     [wagerScroll configDelegate:self selector:@selector(newWagerSelected:)];
    [self addSubview:wagerScroll];
}

@end
