//
//  FResultVC.h
//  facebet
//
//  Created by Kyle on 2014-04-26.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FPayoutView.h"
#import "FBetButtons.h"
#import "FUGameType.h"
#import "FBetPanel.h"
#import "FTeamButtons.h"
#import "FFloatBet.h"
#import "FDoneBetVC.h"
#import "FDateSlash.h"
#import "FPastCircles.h"
#import "FDetailHead.h"
#import "FWagerScroll.h"
#import "FPickPanel.h"


/**
 The VC that's displayed when user clicked a past game to view the past game details
 */

@interface FResultVC : UIViewController<FBetButtonProtocol,FTeamButtonProtocol>{
    NSMutableArray* homePayout;
    NSMutableArray* awayPayout;
    
    BOOL isHomePayout;
}

//The game info UI at the top of the view controller, eg: "Arsenal vs ManChester United"
@property(nonatomic,strong) FDetailHead* detailHead;

@property(nonatomic,strong) FWagerScroll* wagerScroll;

//The UI components that shows which team the user has picked
@property(nonatomic,strong) FPickPanel* pickPanel;

//The two clock circles UI that shows winning/loss & percentages
@property(nonatomic,strong) FPastCircles* pastCircles;

//The UI for the date components on the top left corner
@property(nonatomic,strong) FDateSlash* dateSlash;


@property(nonatomic,strong) UIView* payoutBg;
@property(strong,nonatomic) UIScrollView* betScroll;

//Game model object
@property(nonatomic,assign) FGame* myBet;

//The question type: eg: "Who will win?"
@property(strong,nonatomic) FUGameType* gameTypeHead;

//The red & blue bet buttons that shows which team the user has bet on
@property(strong,nonatomic) FBetButtons* betButtons;

@property(strong,nonatomic) FBetPanel* betPanel;

//The buttons that allow users to pick which payout table they want to view at the bottom
@property(strong,nonatomic) FTeamButtons* teamButtons;

//The payout table for home & away teams
@property(nonatomic,strong) FPayoutView* payoutTable;
@property(nonatomic,strong) FFloatBet* floatBet;

-(void)setupContent;

-(void)homeTeamSelected;
-(void)awayTeamSelected;
@end
