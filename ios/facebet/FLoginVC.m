//
//  FLoginVC.m
//  facebet
//
//  Created by Kun on 2013-12-24.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FLoginVC.h"

#import "UserDetailsViewController.h"

@interface FLoginVC ()

@end

@implementation FLoginVC

@synthesize parentVC,userField,passField,signupBtn;
@synthesize scrollView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.title = @"LOG IN";
        [self.navigationItem setHidesBackButton:YES];
        
        if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }

        
    }
    return self;
}

-(IBAction)facebookLogin:(id)sender{
    /*
    UserDetailsViewController* user = [[UserDetailsViewController alloc] initWithNibName:@"UserDetailsViewController" bundle:[NSBundle mainBundle]];
    
    [self.navigationController pushViewController:user animated:YES];
     */
    
    // Set permissions required from the facebook user account
        NSArray *permissionsArray = @[ @"user_about_me", @"user_relationships", @"user_birthday", @"user_location"];
    
    // Login PFUser using facebook
    [PFFacebookUtils logInWithPermissions:permissionsArray block:^(PFUser *user, NSError *error) {
            if (!user) {
                if (!error) {
                    
                    NSLog(@"Uh oh. The user cancelled the Facebook login.");
                    
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Log In Error" message:@"Uh oh. The user cancelled the Facebook login." delegate:nil cancelButtonTitle:nil otherButtonTitles:@"Dismiss", nil];
                    
                    [alert show];
                    
                } else {
                    
                    NSLog(@"Uh oh. An error occurred: %@", error);
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Log In Error" message:[error description] delegate:nil cancelButtonTitle:nil otherButtonTitles:@"Dismiss", nil];
                    [alert show];
                }
                
            } else if (user.isNew) {
                NSLog(@"User with facebook signed up and logged in!");
                [self.navigationController pushViewController:[[UserDetailsViewController alloc] initWithStyle:UITableViewStyleGrouped] animated:YES];
            } else {
                
                NSLog(@"User with facebook logged in!");
                [self.navigationController pushViewController:[[UserDetailsViewController alloc] initWithStyle:UITableViewStyleGrouped] animated:YES];
            }
        }];
    
}

-(IBAction)doLogin:(id)sender{
    [self dismissAll];
    
    NSString* user = userField.text;
    NSString* pass = passField.text;
    [[MNetwork sharedInstance] login:self user:user pass:pass success:@selector(loginSuccess:) failure:@selector(loginFailed:)];
    
    hud = [MBProgressHUD showHUDAddedTo:self.navigationController.view animated:YES] ;
    hud.delegate = self;
    [hud setLabelText:@"Logging In..."];
}


-(void)loginSuccess:(id)obj{
//    sfv(obj)
    [hud hide:YES];
    [[FGlobal sharedInstance] setCredential:obj];
    [parentVC performSelector:NSSelectorFromString(@"switchToStat:") withObject:obj afterDelay:0];
}

-(void)loginFailed:(id)obj{
    [hud hide:YES];
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Login Failed" delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}

-(IBAction)signupClicked:(id)sender{
    [parentVC performSelector:NSSelectorFromString(@"switchToSignUp") withObject:Nil afterDelay:0];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [[signupBtn layer] setBorderColor:[UIColor fGreenColor].CGColor];
    [[signupBtn layer] setBorderWidth:1.0];

    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(dismissAll)];
    
    [self.view addGestureRecognizer:tap];
    

 
}

-(void)dismissAll{
    [userField resignFirstResponder];
    [passField resignFirstResponder];
}

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
}

-(void)awakeFromNib{
    
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self registerKeyboardNotifications];

    [scrollView setFrame:CGRectMake(0, 0, 320, 350+[MUtil extraHeight])];
    [scrollView setContentSize:CGSizeMake(320, 350)];
    [scrollView setNeedsLayout];
}

-(void)viewWillDisappear:(BOOL)animated{
    [self unregisterKeyboardNotifications];
    [super viewWillDisappear:animated];
}


- (void)registerKeyboardNotifications {
    // Register for keyboard notifications
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillShow:)
                                                 name:UIKeyboardWillShowNotification
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];
}

- (void)unregisterKeyboardNotifications {
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:UIKeyboardWillShowNotification
                                                  object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:UIKeyboardWillHideNotification
                                                  object:nil];
}


- (void)keyboardWillShow:(NSNotification*)aNotification {
    _showKeyboard = YES;
    [self.view adjustKeyboard:aNotification flag:_showKeyboard];
}

- (void)keyboardWillHide:(NSNotification*)aNotification {
    _showKeyboard = NO;
    [self.view adjustKeyboard:aNotification flag:_showKeyboard];
}


- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    BOOL val;
    if(textField == userField){
        val = NO;
        [passField becomeFirstResponder];
    }else{
        val = YES;
        [userField resignFirstResponder];
        [passField resignFirstResponder];
        [self doLogin:nil];
    }
    return val;
}

- (void)textFieldDidBeginEditing:(UITextField *)textField{
    [scrollView scrollRectToVisible:textField.frame animated:YES];
}



- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
