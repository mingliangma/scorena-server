//
//  FRangeButtons.m
//  facebet
//
//  Created by Kyle on 2014-05-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FRangeButtons.h"

@implementation FRangeButtons

@synthesize weekBtn,monthBtn,allBtn,parentVC;

- (id)initWithPoint:(CGPoint)point{
    self = [[[NSBundle mainBundle] loadNibNamed:@"FRangeButtons" owner:nil options:nil] firstObject];
    [self setFrame:CGRectMake(point.x, point.y,self.frame.size.width , self.frame.size.height)];
    return self;
}

-(void)resetColors{
    [[monthBtn titleLabel] setTextColor:[UIColor fMiddleGrayColor]];
    [[allBtn titleLabel] setTextColor:[UIColor fMiddleGrayColor]];
    [[weekBtn titleLabel] setTextColor:[UIColor fMiddleGrayColor]];
}


-(IBAction)weekClicked:(id)sender{
    [self resetColors];    
    [weekBtn setTitleColor:[UIColor fBlueColor] forState:UIControlStateNormal];
    [parentVC performSelector:NSSelectorFromString(@"loadWeek") withObject:nil afterDelay:0];
}

-(IBAction)monthClicked:(id)sender{
    [self resetColors];
    [monthBtn setTitleColor:[UIColor fBlueColor] forState:UIControlStateNormal];
    [parentVC performSelector:NSSelectorFromString(@"loadMonth") withObject:nil afterDelay:0];
}

-(IBAction)allClicked:(id)sender{
    [self resetColors];
    [allBtn setTitleColor:[UIColor fBlueColor] forState:UIControlStateNormal];
    [parentVC performSelector:NSSelectorFromString(@"loadAll") withObject:nil afterDelay:0];
}


@end
