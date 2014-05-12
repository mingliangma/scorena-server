//
//  UINavigationController+Custome.m
//  facebet
//
//  Created by Kyle on 2014-05-03.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "UINavigationController+Custome.h"

@implementation UINavigationController (Custome)


-(void)configureWithTitle:(NSString*)title{
    
     NSDictionary *navbarTitleTextAttributes = [NSDictionary dictionaryWithObjectsAndKeys:
     [UIColor whiteColor],UITextAttributeTextColor, nil];
     
     [self.navigationBar setTitleTextAttributes:navbarTitleTextAttributes];
    self.navigationBar.tintColor = [UIColor whiteColor];
    
    if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
        [self.navigationBar setBarTintColor:[UIColor fTopBlueColor]];
    }else{
        [self.navigationBar setTintColor:[UIColor fTopBlueColor]];
    }
    
    self.title = [title copy];
}
@end
