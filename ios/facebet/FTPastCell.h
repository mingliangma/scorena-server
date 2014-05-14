//
//  FTPastCell.h
//  facebet
//
//  Created by Kun on 2013-12-29.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FCharts.h"
#import "FUGameType.h"
#import "FBetButtons.h"
#import "FPayoutView.h"
#import "FTeamButtons.h"

/**
    The Cell that displays a past game where user can only view but can't click to bet
 */

@interface FTPastCell : UIView<FTeamButtonProtocol>{
    NSArray* homePayout;
    NSArray* awayPayout;
    BOOL isHomePayout;
}

- (id)init;
@property(assign,nonatomic) BOOL isOpen;
@property(assign,nonatomic) FGame* myGame;
@property(strong,nonatomic) FUGameType* gameTypeHead;
@property(strong,nonatomic) FBetButtons* betButtons;
@property(strong,nonatomic) NSIndexPath* path;
@property(strong,nonatomic) UIButton* overBtn;

@property(assign,nonatomic) id parent;
@property(assign,nonatomic) SEL adjustSEL;

-(void)setupGame:(id)obj handler:(id<FTeamButtonProtocol>)handle;
-(void)initPastCell;

@end
