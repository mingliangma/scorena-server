//
//  FDummyHead.m
//  facebet
//
//  Created by Kyle on 2014-05-03.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FDummyHead.h"

@implementation FDummyHead

- (id)initWithPoint:(CGPoint)point{
    self = [[[NSBundle mainBundle] loadNibNamed:@"FDummyHead" owner:nil options:nil] firstObject];
    [self setFrame:CGRectMake(point.x, point.y, self.frame.size.width, self.frame.size.height)];
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
