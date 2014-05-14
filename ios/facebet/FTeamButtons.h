//
//  FTeamButtons.h
//  facebet
//
//  Created by Kun on 2013-12-30.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

/**
    Team buttons to select which payout table the user wants to see at the bottom of FBettingVC & FResultVC
 */

@protocol FTeamButtonProtocol <NSObject>

@optional
-(void)homePayoutSelected;
-(void)awayPayoutSelected;

@end

@interface FTeamButtons : UIView{
    BOOL isPast;
}

//Crowd betting segments
@property(nonatomic,strong) UIButton* homeCrowd;
@property(nonatomic,strong) UIButton* awayCrowd;
@property(nonatomic,assign) BOOL homeWager;
@property(nonatomic,assign) id<FTeamButtonProtocol> delegate;
@property(nonatomic,assign) SEL chooseSel;

-(void)setupContent:(id)obj;
-(void)setupIsPast;

-(void)wagerSelect:(id)sender;
@end
