//
//  FBettingVC.h
//  facebet
//
//  Created by Kun on 2013-12-19.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "FWagerScroll.h"
#import "FPayoutView.h"
#import "FBetButtons.h"
#import "FUGameType.h"
#import "FBetPanel.h"
#import "FTeamButtons.h"
#import "FFloatBet.h"
#import "FDoneBetVC.h"
#import "FDateSlash.h"
#import "FDetailHead.h"
#import "FReloadView.h"
#import "FAppDelegate.h"

/**
 The VC that's displayed when user clicked an upcoming game to view the game details
 Users can view odds and place bet in here
 */


@interface FBettingVC : UIViewController<FBetButtonProtocol,FTeamButtonProtocol,UIScrollViewDelegate,UIAlertViewDelegate>{
    NSMutableArray* homePayout;
    NSMutableArray* awayPayout;

    kBetSelectType mySelect;
    BOOL isHomePayout;
    
    BOOL scrollLock;
}

@property(nonatomic,strong) FReloadView* reloadView;

@property(nonatomic,strong) UIView* payoutBg;
@property(strong,nonatomic) UIScrollView* betScroll;

//Game model object
@property(nonatomic,copy) FGame* myBet;

@property(nonatomic,strong) FDetailHead* detailHead;

//Date UI at the left top corner
@property(nonatomic,strong) FDateSlash* dateSlash;

//Game Question Header, eg:"Who will win?"
@property(strong,nonatomic) FUGameType* gameTypeHead;

//The red and blue team buttons in the middle, with number of plays and also odds
@property(strong,nonatomic) FBetButtons* betButtons;

//Panel that includes the odds & wager scroll in the middle, the betting chips are also in here
@property(strong,nonatomic) FBetPanel* betPanel;

//Buttons to view the team payout tables
@property(strong,nonatomic) FTeamButtons* teamButtons;

//Area to show the list of users & payouts
@property(nonatomic,strong) FPayoutView* payoutTable;

//Betting button at the bottom with countdown
@property(nonatomic,strong) FFloatBet* floatBet;

-(void)setupContent;

-(void)homeTeamSelected;
-(void)awayTeamSelected;
-(void)noTeamSelected;

-(void)dismissDoneVC;

@end
