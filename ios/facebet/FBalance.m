//
//  FBalance.m
//  facebet
//
//  Created by Kyle on 2014-05-04.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FBalance.h"
#import "FBankVC.h"

@implementation FBalance

@synthesize lb,vc;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {

        UIImageView* img = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"white_coin.png"]];
        [img setCenter:CGPointMake(20, 20)];
        [self addSubview:img];
        
        lb = [[UIButton alloc] initWithFrame:CGRectMake(25, 0, 55, 40)];
        [lb setBackgroundColor:[UIColor clearColor]];
        [lb setTitle:[NSString stringWithFormat:@"%d",[[FGlobal sharedInstance] balance]] forState:UIControlStateNormal];
        [[lb titleLabel] setTextAlignment:NSTextAlignmentRight];
        [lb setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [lb addTarget:self action:@selector(goToBank) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:lb];
        

        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshMyself) name:kRefreshBalanceNotification object:nil];
    }
    return self;
}

- (void) dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
}

-(void)goToBank{
    FBankVC* vc2 = [[FBankVC alloc] initWithNibName:@"FBankVC" bundle:[NSBundle mainBundle]];
    [vc.navigationController pushViewController:vc2 animated:YES];
}

-(void)refreshMyself{
    [lb setTitle:[NSString stringWithFormat:@"%d",[[FGlobal sharedInstance] balance]] forState:UIControlStateNormal];
}

@end
