//
//  FAccountController.m
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FAccountController.h"
#import "FLoginVC.h"
#import "FSignUpVC.h"

@interface FAccountController ()

@end

@implementation FAccountController

@synthesize parentVC,scroll;

@synthesize userField,emailField;
//@synthesize genderField,regionField;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.title = @"Profile";
        self.navigationItem.backBarButtonItem.tintColor = [UIColor whiteColor];
        if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self refreshCredentials];
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    
    [scroll setFrame:CGRectMake(0, 0, 320, 350+[MUtil extraHeight])];
    [scroll setContentSize:CGSizeMake(320, 420)];
    [scroll setNeedsLayout];
}

-(void)refreshCredentials{
    id cred = [[FGlobal sharedInstance] credential];
    [userField setText:[[cred objectForKey:@"username"] copy]];
 //   [regionField setText:[[cred objectForKey:@"region"] copy]];
  //  [genderField setText:[[cred objectForKey:@"gender"] copy]];
    [emailField setText:[[cred objectForKey:@"email"] copy]];
}

-(void)resetSuccess{
    [MUtil showAlert:@"A password reset email has been sent, please check your mailbox." title:@"Success" del:self];
}

-(void)resetFailed{
    [MUtil showAlert:@"Password reset failed, please try again later." del:self];
}

-(IBAction)resetPassword:(id)sender{
    [[MNetwork sharedInstance] resetPassword:self success:@selector(resetSuccess) failure:@selector(resetFailed)];    
}

-(IBAction)signOut:(id)sender{
    [[FGlobal sharedInstance] logout];
    [parentVC performSelector:NSSelectorFromString(@"switchToLogin") withObject:nil afterDelay:0];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
