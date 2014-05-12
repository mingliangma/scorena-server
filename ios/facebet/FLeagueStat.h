//
//  FLeagueStat.h
//  facebet
//
//  Created by Kyle on 2014-05-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FLeagueStat : UIView


IBElement UILabel* winLB;
IBElement UILabel* lossLB;
IBElement UILabel* tieLB;

IBElement UILabel* titleLB;
IBElement UIView* leftBar;
IBElement UIView* rightBar;

- (id)initWithPoint:(CGPoint)point;
-(void)configureWithJsonObj:(id)obj title:(NSString*)title;

@end
