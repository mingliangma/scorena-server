//
//  FSectionHead.h
//  facebet
//
//  Created by Kun on 2013-12-29.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FDateSlash.h"

/**
    The reusable UI that shows the game info, eg: "Arsenal vs Liverpool" 
    - Used as a header in the upcoming & past game table views
    - Used as a top UI in the game detail screen, FBettingVC & FResultVC
 */

@interface FSectionHead : UIView

-(id)initWithFrame:(CGRect)frame game:(FGame*)myGame;

@property(nonatomic,strong) FDateSlash* slash;
@property(nonatomic,strong) FGame* game;

@property(nonatomic,strong) UIView* topBar;
@property(nonatomic,strong) NSString* teamA;
@property(nonatomic,strong) NSString* teamB;

@property(nonatomic,strong) UILabel* aLabel;
@property(nonatomic,strong) UILabel* bLabel;

@property(nonatomic,strong) UILabel* sign;
@property(nonatomic,strong) UILabel* vsLabel;

@property(nonatomic,strong) UILabel* statLB;

@property(nonatomic,strong) UIImageView* winLose;



-(void)plus;
-(void)minus;

@end
