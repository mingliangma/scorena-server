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

@interface FResultVC : UIViewController<FBetButtonProtocol,FTeamButtonProtocol>{
    NSMutableArray* homePayout;
    NSMutableArray* awayPayout;
    
    BOOL isHomePayout;
}

@property(nonatomic,strong) FDetailHead* detailHead;
@property(nonatomic,strong) FWagerScroll* wagerScroll;
@property(nonatomic,strong) FPickPanel* pickPanel;
@property(nonatomic,strong) FPastCircles* pastCircles;
@property(nonatomic,strong) FDateSlash* dateSlash;


@property(nonatomic,strong) UIView* payoutBg;

@property(strong,nonatomic) UIScrollView* betScroll;
@property(nonatomic,assign) FGame* myBet;
@property(strong,nonatomic) FUGameType* gameTypeHead;
@property(strong,nonatomic) FBetButtons* betButtons;

@property(strong,nonatomic) FBetPanel* betPanel;
@property(strong,nonatomic) FTeamButtons* teamButtons;
@property(nonatomic,strong) FPayoutView* payoutTable;
@property(nonatomic,strong) FFloatBet* floatBet;

-(void)setupContent;

-(void)homeTeamSelected;
-(void)awayTeamSelected;
@end
