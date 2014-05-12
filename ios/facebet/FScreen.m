//
//  FScreen.m
//  facebet
//
//  Created by Kyle on 2014-04-26.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FScreen.h"

@implementation FScreen

- (id)initWithPoint:(CGPoint)point
{
    self = [super initWithFrame:CGRectMake(point.x, point.y, 300, 140)];
    if (self) {
        // Initialization code
        [self setBackgroundColor:[UIColor colorWithRed:1.0 green:1.0 blue:1.0 alpha:0.2]];
    }
    return self;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
