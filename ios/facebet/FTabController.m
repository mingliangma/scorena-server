//
//  FTabController.m
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FTabController.h"

@interface FTabController ()

@end

@implementation FTabController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        
        [self setDelegate:self];
        
    }
    return self;
}

- (BOOL)tabBarController:(UITabBarController *)tabBarController shouldSelectViewController:(UIViewController *)viewController{
    
    id vc = [tabBarController selectedViewController];
    return vc != viewController;
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidLayoutSubviews
{
    CGFloat tabBarHeight = 66.0;
    CGRect frame = self.view.frame;
    self.tabBar.frame = CGRectMake(0, frame.size.height - tabBarHeight, frame.size.width, tabBarHeight);
}

@end
