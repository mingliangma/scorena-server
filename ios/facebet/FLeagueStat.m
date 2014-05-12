//
//  FLeagueStat.m
//  facebet
//
//  Created by Kyle on 2014-05-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FLeagueStat.h"

@implementation FLeagueStat

@synthesize winLB,tieLB,lossLB;

@synthesize titleLB,rightBar,leftBar;

- (id)initWithPoint:(CGPoint)point{
    
    self = [[[NSBundle mainBundle] loadNibNamed:@"FLeagueStat" owner:self options:nil] firstObject];
    [self setFrame:CGRectMake(point.x, point.y, self.frame.size.width, self.frame.size.height)];
    return self;
}

-(void)configureWithJsonObj:(id)obj title:(NSString*)title{
    NSInteger win = [[obj objectForKey:@"wins"] integerValue];
    NSInteger loss = [[obj objectForKey:@"losses"] integerValue];
    NSInteger tie = [[obj objectForKey:@"ties"] integerValue];
    NSInteger netGain = [[obj objectForKey:@"netGain"] integerValue];
    NSInteger netLoss =[[obj objectForKey:@"netLose"] integerValue];
    
    [winLB setText:[NSString stringWithFormat:@"+$%d/%d",netGain,win]];
    [lossLB setText:[NSString stringWithFormat:@"-$%d/%d",netLoss,loss]];
    [tieLB setText:[NSString stringWithFormat:@"$0/%d",tie]];
    [titleLB setText:title];
    
    [self refreshBarWidth:title];
}

-(void)refreshBarWidth:(NSString*)title{

    CGSize textSize = [title sizeWithFont:[titleLB font]];
    CGFloat strikeWidth = textSize.width;
    
    if(strikeWidth > 80){
        CGFloat offset = (strikeWidth-80)/2;
        
        [leftBar setFrame:CGRectMake(leftBar.frame.origin.x, leftBar.frame.origin.y, leftBar.frame.size.width-offset, leftBar.frame.size.height)];
        [rightBar setFrame:CGRectMake(rightBar.frame.origin.x+offset, rightBar.frame.origin.y, rightBar.frame.size.width-offset, rightBar.frame.size.height)];
        
    }
}


@end
