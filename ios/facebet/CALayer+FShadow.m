//
//  CALayer+FShadow.m
//  facebet
//
//  Created by Kun on 2013-12-28.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "CALayer+FShadow.h"

@implementation CALayer (FShadow)

-(void)fShadowSetup{
    [self setShadowColor:[UIColor blackColor].CGColor];
    [self setShadowOffset:CGSizeMake(0.3, 0.3)];
    [self setShadowOpacity:0.7];
}

-(void)fVerticalShadow{
    [self setShadowColor:[UIColor blackColor].CGColor];
    [self setShadowOffset:CGSizeMake(0, 0.6)];
    [self setShadowOpacity:0.7];
}


@end
