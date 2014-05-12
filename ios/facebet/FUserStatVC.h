//
//  FUserStatVC.h
//  facebet
//
//  Created by Kyle on 2014-05-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FUserStatCircles.h"
#import "FReloadView.h"
#import "FRangeButtons.h"
#import "FLeagueTable.h"

@interface FUserStatVC : UIViewController <UIScrollViewDelegate>{
    id profile;
    BOOL scrollLock;
    
    kStatTimeRange statType;
}

IBElement UILabel* nameLB;
IBElement UILabel* genderLocationLB;
IBElement UIImageView* userImage;
IBElement UIButton* summaryBtn;
IBElement UIButton* historyBtn;

@property(nonatomic,weak) id parentVC;
@property(nonatomic,strong) UIScrollView* scroll;

@property(nonatomic,strong) FUserStatCircles* userCircles;
@property(nonatomic,strong) UIView* whiteBg;

@property(nonatomic,strong) FLeagueTable* leagueTable;
@property(nonatomic,strong) FReloadView* reloadView;
@property(nonatomic,strong) FRangeButtons* rangeButtons;

-(IBAction)editClicked:(id)sender;

-(void)refreshUserProfile;

-(void)loadWeek;
-(void)loadMonth;
-(void)loadAll;

@end
