//
//  FBetPanel.h
//  facebet
//
//  Created by Kun on 2013-12-30.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FWagerScroll.h"
#import "FPickPanel.h"


/**
    Re-usable UI Panel that includes the odds & wager scroll in the middle, the betting chips are also in here.
    Used in FBettingVC
 */

@interface FBetPanel : UIView{
    NSInteger poolA;
    NSInteger poolB;
    BOOL didBet;
    kBetSelectType betType;
}

@property(nonatomic,assign) NSInteger wager;

@property(nonatomic,strong) FWagerScroll* wagerScroll;
//Scroll views

@property(nonatomic,strong) FPickPanel* pickPanel;

@property(nonatomic,strong) UILabel* oddsBlue;
@property(nonatomic,strong) UILabel* lastUpdate;

@property(nonatomic,strong) UILabel* expectLB;
@property(nonatomic,strong) UILabel* minimumLB;

@property(nonatomic,strong) UILabel* expect1;

@property(nonatomic,strong) UIImageView* strips;
@property(nonatomic,strong) id gameObj;

-(void)newWagerSelected:(NSNumber*)num;
-(void)configureUpdateTime:(NSString*)str;
-(void)populateWithJson:(id)obj past:(BOOL)alreadyBet team:(kBetSelectType)betSelectType bet:(FGame*)betObj;

-(void)homeSelected;
-(void)awaySelected;

@end
