//
//  FTimeLabel.m
//  facebet
//
//  Created by Kyle on 2014-01-03.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FTimeLabel.h"

@implementation FTimeLabel

@synthesize myDate;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {

        [self setTextAlignment:NSTextAlignmentCenter];
//        [self setText:@"2 Days: 12:15:00"];
        [self setFont:[UIFont fStraightFont:12]];
        [self setTextColor:[UIColor whiteColor]];
    }
    return self;
}


-(void)startWithTime:(NSDate*)date{
    myDate = date;
    [self tick];
    myTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(tick) userInfo:nil repeats:YES];
    [[NSRunLoop currentRunLoop] addTimer:myTimer forMode:NSRunLoopCommonModes];
}

-(void)tick{
    [self setText:[myDate countDownFromNow]];
}

@end
