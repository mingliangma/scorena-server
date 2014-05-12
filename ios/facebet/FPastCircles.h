//
//  FPastCircles.h
//  facebet
//
//  Created by Kyle on 2014-05-01.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FPastCircles : UIView

IBElement UIImageView* percentCircle;
IBElement UIImageView* amountCircle;
IBElement UIImageView* percentPointer;
IBElement UIImageView* amountPointer;
IBElement UILabel* percentLB;
IBElement UILabel* amountLB;
IBElement UILabel* totalLB;

IBElement UILabel* winLB;
IBElement UILabel* payoutLB;

- (id)initWithPoint:(CGPoint)point;

-(void)configureWithType:(FUserPickStatus)pick money:(NSInteger)money percent:(NSInteger)percent;

@end
