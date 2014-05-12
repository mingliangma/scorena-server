//
//  FSignUpVC.h
//  facebet
//
//  Created by Kun on 2013-12-24.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface FSignUpVC : UIViewController<UITextFieldDelegate,MBProgressHUDDelegate>{
    BOOL _showKeyboard;
    NSString* _tmpUser;
    NSString* _tmpPass;
    MBProgressHUD * HUD;
}

IBElement UIScrollView* scrollView;

IBElement UIButton* loginBtn;
@property(nonatomic,assign) id parentVC;

IBElement UITextField* userField;
IBElement UITextField* passField;
IBElement UITextField* emailField;
//IBElement UITextField* regionField;
//IBElement UITextField* genderField;

-(IBAction)signup:(id)sender;

@end
