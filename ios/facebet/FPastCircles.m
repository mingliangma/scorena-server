//
//  FPastCircles.m
//  facebet
//
//  Created by Kyle on 2014-05-01.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FPastCircles.h"

@implementation FPastCircles

@synthesize percentCircle,amountCircle,percentLB,percentPointer,amountLB,amountPointer,winLB,payoutLB,totalLB;

- (id)initWithPoint:(CGPoint)point{
    self = [[[NSBundle mainBundle] loadNibNamed:@"FPastCircles" owner:nil options:nil] objectAtIndex:0];
    [self setFrame:CGRectMake(point.x, point.y, self.frame.size.width, self.frame.size.height)];
    return self;
}

-(void)configureWithType:(FUserPickStatus)pick money:(NSInteger)money percent:(NSInteger)percent{
    
    //Set background circle colors
    if(pick == FUserPickStatusWin){
        [self changeCirclesTo:@"Past_Yellow.png"];
        [totalLB setText:@"TOTAL WINNING"];
    }else if(pick==FUserPickStatusLose){
        [self changeCirclesTo:@"Past_Blue.png"];
        [totalLB setText:@"TOTAL LOSS"];
    }else if(pick==FUserPickStatusTie){
        [self changeCirclesTo:@"Past_Gray.png"];
    }
    
    //Numbers on top of the red circles
    [amountLB setText:[NSString stringWithFormat:@"$%d",money]];
    [percentLB setText:[NSString stringWithFormat:@"%d%%",percent]];
    
    CGFloat degree = money/5.0;
    CGFloat percentDegree = percent*3.6;
    
    [self.amountPointer spinToDegree:degree];
    [self.percentPointer spinToDegree:percentDegree];
    
    [winLB setText:[NSString stringWithFormat:@"$%d",money]];
    [payoutLB setText:[NSString stringWithFormat:@"+%d%%",percent]];
}

-(void)changeCirclesTo:(NSString*)image{
    [percentCircle setImage:[UIImage imageNamed:image]];
    [amountCircle setImage:[UIImage imageNamed:image]];
}

@end
