//
//  FFloatBet.m
//  facebet
//
//  Created by Kun on 2013-12-30.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FFloatBet.h"

@implementation FFloatBet

@synthesize bottomStripe,betButton,releaseLB,del,sel,timeLB;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        [self loadUI];
    }
    return self;
}

-(void)configWithDelegate:(id)delegate selector:(SEL)selector{
    del = delegate;
    sel = selector;
}

-(void)doYourThing{
    if([del respondsToSelector:sel]){
        [del performSelector:sel withObject:nil afterDelay:0];
    }
}

-(void)loadUI{
    bottomStripe = [[UIView alloc] initWithFrame:CGRectMake(0, 10, 320, 60)];
    [bottomStripe setBackgroundColor:[UIColor fDarkBottomGreen]];
    
    releaseLB = [[UILabel alloc] initWithFrame:CGRectMake(30, 7, 140, 30)];
    [releaseLB setText:@"Result Released in"];
    [releaseLB setFont:[UIFont fStraightFont:10]];
    [releaseLB setTextColor:[UIColor whiteColor]];
    
    betButton = [[UIButton alloc] initWithFrame:CGRectMake(160, 20, 142, 30)];
    [betButton setBackgroundColor:[UIColor fRedColor]];
    [betButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [betButton setTitle:@"BET NOW" forState:UIControlStateNormal];
    [[betButton titleLabel] setFont:[UIFont fStraightFont:12]];
    [betButton addTarget:self action:@selector(doYourThing) forControlEvents:UIControlEventTouchUpInside];
    [[betButton layer] fShadowSetup];
    
    timeLB = [[FTimeLabel alloc] initWithFrame:CGRectMake(0, 20, 140, 40)];
    [timeLB setTextAlignment:NSTextAlignmentCenter];
    [timeLB setFont:[UIFont fStraightFont:12]];
    [timeLB setTextColor:[UIColor whiteColor]];
    
    [self addSubview:bottomStripe];
    [self addSubview:releaseLB];
    [self addSubview:betButton];
    [self addSubview:timeLB];
}
-(void)startTime:(NSDate*)time{
    [timeLB startWithTime:time];
}

@end
