//
//  FMoreController.m
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FMoreController.h"
#import "FMoreTableController.h"

@interface FMoreController ()

@end

@implementation FMoreController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
       
        [self configureWithTitle:@"More"];
        
        FMoreTableController* moretable = [[FMoreTableController alloc] initWithNibName:@"FMoreTableController" bundle:[NSBundle mainBundle]];
        [self pushViewController:moretable animated:NO];
        
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
