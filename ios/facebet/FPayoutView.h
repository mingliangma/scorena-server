//
//  FPayoutView.h
//  facebet
//
//  Created by Kun on 2013-12-29.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>


/**
    The payout table for home & away teams in FBettingVC & FResultVC
 */

@interface FPayoutView : UIView{
    NSArray* list;
    NSUInteger shown;
}

@property(nonatomic,assign) id parent;
@property(nonatomic,assign) SEL refreshSEL;

@property(nonatomic,strong) UIView* topBorder;
@property(nonatomic,strong) UIView* bottomLeft;
@property(nonatomic,strong) UIView* bottomRight;

- (id)initWithFrame:(CGRect)frame array:(NSArray*)array;

-(void)clean;

@end
