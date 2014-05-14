//
//  FDateSlash.h
//  facebet
//
//  Created by Kyle on 2014-04-24.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>


/**
    The date UI to display month, date & year & time for a game, used in many places
 */

@interface FDateSlash : UIView

- (id)initWithFrame:(CGRect)frame game:(FGame*)myGame;

@property(nonatomic,strong) FGame* game;
@property(strong,nonatomic) UIImageView* slashGray;
@property(strong,nonatomic) UIView* redTime;

@property(nonatomic,strong) UILabel* monthLabel;
@property(nonatomic,strong) UILabel* yearLabel;
//@property(nonatomic,strong) UILabel* timeLB;


@end
