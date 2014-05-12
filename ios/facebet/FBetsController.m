//
//  FBetsController.m
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FBetsController.h"
#import "FGameController.h"

@interface FBetsController ()

@end

@implementation FBetsController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        
 
        [self configureWithTitle:@"Games"];        
        FGameController* game = [[FGameController alloc] initWithNibName:@"FGameController" bundle:[NSBundle mainBundle]];
        [super pushViewController:game animated:NO];
        
    }
    return self;
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

@end
