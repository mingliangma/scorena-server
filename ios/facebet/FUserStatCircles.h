//
//  FUserStatCircles.h
//  facebet
//
//  Created by Kyle on 2014-05-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FUserStatCircles : UIView

IBElement UILabel* currentLB;
IBElement UILabel* gainLB;
IBElement UILabel* pctLB;
IBElement UIImageView* pointer;

IBElement UILabel* winLB;
IBElement UILabel* lossLB;
IBElement UILabel* tieLB;

- (id)initWithPoint:(CGPoint)point;

-(void)configureWithTimeRange:(id)obj balance:(NSInteger)balance;

@end
