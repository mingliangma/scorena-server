//
//  UIScrollView+Scrolling.m
//  facebet
//
//  Created by Kyle on 2014-04-19.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "UIScrollView+Scrolling.h"

@implementation UIScrollView (Scrolling)

-(void)scrollByY:(CGFloat)num{
    [self setContentOffset:CGPointMake(0, num) animated:YES];
}

@end
