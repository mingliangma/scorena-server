//
//  FBalance.h
//  facebet
//
//  Created by Kyle on 2014-05-04.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FAppDelegate.h"

/**
    The Re-used UI component to display the bank balance
        - At the top right corner
        - On the bank view controller 
 
    This component will automatically update itself when it receives a kRefreshBalanceNotification,such that we don't need to find all UIViews 
    and update each one of them.
 */

@interface FBalance : UIView

@property(nonatomic,strong) UIButton* lb;

@property(nonatomic,weak) UIViewController* vc;

-(void)refreshMyself;

@end
