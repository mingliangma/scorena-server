//
//  FPickPanel.h
//  facebet
//
//  Created by Kyle on 2014-05-06.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FPickPanel : UIView

@property(nonatomic,strong) UILabel* decideLB;
@property(nonatomic,strong) UIView* decideLeft;
@property(nonatomic,strong) UIView* decideRight;
@property(nonatomic,strong) UIView* decideMiddle;
@property(nonatomic,strong) UILabel* pick;

- (id)initWithPoint:(CGPoint)point;
-(void)configureWithPick:(NSString*)myPick;
-(void)setupGrayUIWhenBetPlaced;
@end
