//
//  FFloatBet.h
//  facebet
//
//  Created by Kun on 2013-12-30.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FTimeLabel.h"

@interface FFloatBet : UIView

//Bottom floating bet button
@property(nonatomic,strong) UIView* bottomStripe;
@property(nonatomic,strong) UIButton* betButton;
@property(nonatomic,strong) UILabel* releaseLB;
@property(nonatomic,strong) FTimeLabel* timeLB;

@property(nonatomic,assign) id del;
@property(nonatomic,assign) SEL sel;

-(void)configWithDelegate:(id)delegate selector:(SEL)selector;
-(void)startTime:(NSDate*)time;

@end
