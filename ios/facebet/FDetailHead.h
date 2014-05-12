//
//  FDetailHead.h
//  facebet
//
//  Created by Kyle on 2014-05-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FDetailHead : UIView

IBElement UILabel* statusLB;
IBElement UILabel* teamA;
IBElement UILabel* teamB;
IBElement UILabel* vsLB;

-(void)configureWithGame:(FGame*)game;
- (id)initWithPoint:(CGPoint)point;
-(void)updateGameScoreWithJsonObj:(id)obj;

@end
