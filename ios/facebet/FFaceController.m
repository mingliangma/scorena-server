//
//  FFaceController.m
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FFaceController.h"

#import "FLoginVC.h"
#import "FSignUpVC.h"

@interface FFaceController ()

@end

@implementation FFaceController


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
               
        [self configureWithTitle:@"Profile"];
        [self chooseVC];
    }
    return self;
}


-(void)chooseVC{
    login = [[FLoginVC alloc] initWithNibName:@"FLoginVC" bundle:[NSBundle mainBundle]];
    [login setParentVC:self];
    [self pushViewController:login animated:NO];
    
    if([[FGlobal sharedInstance] authenticated]){
        [self switchToStat:nil];
    }
   
}

-(void)switchToStat:(id)obj{
    stat = [[FUserStatVC alloc] initWithNibName:@"FUserStatVC" bundle:[NSBundle mainBundle]];
    [stat setParentVC:self];
    [stat refreshUserProfile];
    [self pushViewController:stat animated:YES];
}

-(void)switchToProfile:(id)obj{    
    account = [[FAccountController alloc] initWithNibName:@"FAccountController" bundle:[NSBundle mainBundle]];
    [account refreshCredentials];
    [account setParentVC:self];
    [self pushViewController:account animated:YES];
}

-(void)switchToLogin{
    [self popToRootViewControllerAnimated:NO];
}

-(void)switchToSignUp{
    [self goSignUp];
}


-(void)goSignUp{
    FSignUpVC* signup = [[FSignUpVC alloc] initWithNibName:@"FSignUpVC" bundle:[NSBundle mainBundle]];
    [signup setParentVC:self];
    [self pushViewController:signup animated:YES];
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
