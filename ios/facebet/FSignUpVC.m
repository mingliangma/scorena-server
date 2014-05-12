//
//  FSignUpVC.m
//  facebet
//
//  Created by Kun on 2013-12-24.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FSignUpVC.h"

@interface FSignUpVC ()

@end

@implementation FSignUpVC

@synthesize parentVC;
@synthesize userField,passField,emailField,loginBtn;
//@synthesize regionField,genderField;
@synthesize scrollView;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.title = @"SIGN UP";
        
        if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }
                 self.navigationItem.backBarButtonItem.tintColor = [UIColor whiteColor];

    }
    return self;
}


-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
}


-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    [self registerKeyboardNotifications];

    [scrollView setFrame:CGRectMake(0, 0, 320, 350+[MUtil extraHeight])];
    [scrollView setContentSize:CGSizeMake(320,490)];
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
    NSDictionary* info = [aNotification userInfo];
    CGRect kbFrameEndFrame = [[info objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    NSTimeInterval animDuration = [[info objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    _showKeyboard = YES;
    [self adjustUIForKeyboard:kbFrameEndFrame.size animDuration:animDuration];
}

- (void)keyboardWillHide:(NSNotification*)aNotification {
    NSDictionary* info = [aNotification userInfo];
    CGRect kbFrameEndFrame = [[info objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    NSTimeInterval animDuration = [[info objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    _showKeyboard = NO;
    [self adjustUIForKeyboard:kbFrameEndFrame.size animDuration:animDuration];
}

- (void)adjustUIForKeyboard:(CGSize)keyboardSize animDuration:(NSTimeInterval)duration {
    
    CGFloat delta=125;
    if(UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone)
    {
        CGSize result = [[UIScreen mainScreen] bounds].size;
        if(result.height == 480)
        {
            // iPhone Classic
//            delta = 125;
            delta = 70;
        }
        if(result.height == 568)
        {
            delta = 80;
            // iPhone 5
        }
    }
    if (_showKeyboard) {
        [self.view moveUpBy:keyboardSize.height-delta duration:duration];
    } else {
        [self.view moveDownBy:keyboardSize.height-delta duration:duration];
    }
}

- (void)textFieldDidBeginEditing:(UITextField *)textField{

    CGRect frame = [textField superview].frame;
    CGFloat num = frame.origin.y-210;
    
    CGFloat diff = scrollView.contentSize.height - scrollView.frame.size.height;
    
    if(num <diff && textField != userField){
        [scrollView setContentOffset:CGPointMake(0, num) animated:YES];
    }
}


- (BOOL)textFieldShouldReturn:(UITextField *)textField{
    
    BOOL val;
    if(textField == userField){
        val = NO;
        [emailField becomeFirstResponder];
    }else if(textField == emailField){
        val = NO;
   //     [genderField becomeFirstResponder];

   // }
    
    //else if(textField == genderField){
     //   val = NO;
      //  [regionField becomeFirstResponder];
   // }else if(textField == regionField){
    //    val = NO;
    //    [passField becomeFirstResponder];
    }else{
        val = YES;
        //  [self startLogin];
        [self dismissAll];
        [self signup:nil];
    }
    return val;
}

-(IBAction)signup:(id)sender{
    NSString* user = userField.text;
    NSString* email = emailField.text;
 //   NSString* gender = genderField.text;
  //  NSString* region = regionField.text;
    NSString* pass = passField.text;
 
    NSMutableDictionary* dict = [[NSMutableDictionary alloc] init];
    
    [dict setObject:user forKey:SU_UserKey];
    [dict setObject:email forKey:SU_EmailKey];
 //   [dict setObject:gender forKey:SU_GenderKey];
  //  [dict setObject:region forKey:SU_RegionKey];
    [dict setObject:pass forKey:SU_PassKey];
    
    [[MNetwork sharedInstance] signup:self dict:dict success:@selector(signupSuccess:) failure:@selector(signupFailed:)];
   	HUD = [MBProgressHUD showHUDAddedTo:self.navigationController.view animated:YES] ;
    HUD.delegate = self;
    [HUD setLabelText:@"Signing Up..."];
}

-(void)signupSuccess:(id)obj{
    [HUD hide:YES];
    
    [[FGlobal sharedInstance] setCredential:obj];
    [parentVC performSelector:NSSelectorFromString(@"switchToStat:") withObject:obj afterDelay:0];
}

-(void)signupFailed:(id)obj{
    [HUD hide:YES];
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"Error" message:[NSString stringWithFormat:@"%@",obj] delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}

- (void)hudWasHidden:(MBProgressHUD *)hud {
    // Remove HUD from screen when the HUD was hidded
    [HUD removeFromSuperview];
    HUD = nil;
}

-(void)dismissAll{
    [emailField resignFirstResponder];
    [userField resignFirstResponder];
    [passField resignFirstResponder];
 //   [regionField resignFirstResponder];
  //  [genderField resignFirstResponder];
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    [[loginBtn layer] setBorderColor:[UIColor fGreenColor].CGColor];
    [[loginBtn layer] setBorderWidth:1.0];

    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(dismissAll)];
    
    [self.view addGestureRecognizer:tap];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
