//
//  FBalance.h
//  facebet
//
//  Created by Kyle on 2014-05-04.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FAppDelegate.h"

@interface FBalance : UIView

@property(nonatomic,strong) UIButton* lb;

@property(nonatomic,weak) UIViewController* vc;

-(void)refreshMyself;

@end
