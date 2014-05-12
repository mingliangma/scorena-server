//
//  FAccountController.h
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FAccountController : UIViewController

@property(nonatomic,assign) id parentVC;

IBElement UIScrollView* scroll;

IBElement UITextField* userField;
IBElement UITextField* emailField;
//IBElement UITextField* genderField;
//IBElement UITextField* regionField;

-(IBAction)resetPassword:(id)sender;
-(IBAction)signOut:(id)sender;
-(void)refreshCredentials;

@end
