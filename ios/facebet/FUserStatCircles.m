//
//  FUserStatCircles.m
//  facebet
//
//  Created by Kyle on 2014-05-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FUserStatCircles.h"

@implementation FUserStatCircles

@synthesize pointer,currentLB,gainLB,pctLB,winLB,lossLB,tieLB;

- (id)initWithPoint:(CGPoint)point{
    self = [[[NSBundle mainBundle] loadNibNamed:@"FUserStatCircles" owner:self options:nil] firstObject];
    [self setFrame:CGRectMake(point.x, point.y, self.frame.size.width,self.frame.size.height)];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refresh) name:kRefreshBalanceNotification object:nil];
    
    return self;
}
- (void) dealloc{    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
}


-(void)refresh{
    [currentLB setText:[NSString stringWithFormat:@"%d",[[FGlobal sharedInstance] balance]]];
}

-(void)configureWithTimeRange:(id)obj balance:(NSInteger)balance{  
    [currentLB setText:[NSString stringWithFormat:@"%d",balance]];
    [winLB setText:[NSString numStringForKey:@"wins" obj:obj]];
    [lossLB setText:[NSString numStringForKey:@"losses" obj:obj]];
    [tieLB setText:[NSString numStringForKey:@"ties" obj:obj]];
    [gainLB setText:[NSString numStringForKey:@"netGain" obj:obj]];
    [pctLB setText:[NSString stringWithFormat:@"+%@%%",[NSString numStringForKey:@"netGainPercent" obj:obj]]];
    
    CGFloat pct =[[obj objectForKey:@"netGainPercent"] floatValue];
    [pointer spinToDegree:pct*3.6];
}


@end
