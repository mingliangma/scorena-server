//
//  UIFont+FFonts.m
//  facebet
//
//  Created by Kun on 2013-12-28.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "UIFont+FFonts.h"

@implementation UIFont (FFonts)

+(UIFont*)fStraightFont:(CGFloat)size{
    return [UIFont fontWithName:@"Lato-Bold" size:size];
}

+(UIFont*)fItalicFont:(CGFloat)size{
    return [UIFont fontWithName:@"Lato-Italic" size:size];
}

+(UIFont*)fBoldItalicFont:(CGFloat)size{
    return [UIFont fontWithName:@"Lato-Bold" size:size];
}

@end
