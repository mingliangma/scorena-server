//
//  UIColor+FColors.m
//  facebet
//
//  Created by Kun on 2013-12-28.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "UIColor+FColors.h"

@implementation UIColor (FColors)

+ (UIColor *)fBlueColor{
    return [UIColor colorWithRed:0.0 green:110.0/255.0 blue:184.0/255.0 alpha:1.0];
//    return [UIColor colorWithRed:0.0 green:110.0/255.0 blue:184.0/255.0 alpha:1.0];
}

+ (UIColor *)fTopBlueColor{
//    return [UIColor colorWithRed:0.0 green:109.0/255.0 blue:184.0/255.0 alpha:1.0];
    return UIColorFromRGB(0x006db8);
}

+(UIColor*) fAlwaysGray{
    return [UIColor colorWithRed:115.0/255.0 green:122.0/255.0 blue:129.0/255.0 alpha:1.0];
}

+(UIColor*)fRedColor{
//    return [UIColor colorWithRed:160.0/255.0 green:18.0/255.0 blue:21.0/255.0 alpha:1.0];
    return UIColorFromRGB(0x9f1215);
}

+(UIColor*)fDarkGreen{
    return [UIColor fBlueColor];
}

+(UIColor*)fDarkBottomGreen{
    return [UIColor colorWithRed:14.0/255.0 green:77.0/255.0 blue:79.0/255.0 alpha:1.0];
}

+(UIColor*)fGreenColor{
    return [UIColor colorWithRed:0.0 green:174.0/255.0 blue:175.0/255.0 alpha:1.0];
}

+(UIColor*)fGrayColor{
    return [UIColor grayColor];
}

+(UIColor*)fMiddleGrayColor{
    return [UIColor colorWithRed:199.0/255.0 green:194.0/255.0 blue:194.0/255.0 alpha:1.0];
}


+(UIColor*)fLightGrayColor{
    return [UIColor colorWithRed:0.4 green:0.4 blue:0.4 alpha:0.5];
}

+(UIColor*)fVeryLightColor{
    return [UIColor colorWithRed:0.8 green:0.8 blue:0.8 alpha:0.5];
}

+(UIColor*)fDarkGrayColor{
    return [UIColor colorWithRed:0.4 green:0.4 blue:0.4 alpha:0.8];
}



+(UIColor*)fOrangeColor{
    return [UIColor colorWithRed:253.0/255.0 green:163.0/255.0 blue:53.0/255.0 alpha:1];
}

+(UIColor*)fMiddleGreen{
   return [UIColor colorWithRed:74.0/255.0 green:148.0/255.0 blue:199.0/255.0 alpha:1.0];
   // return [UIColor fBlueColor];
}

@end
