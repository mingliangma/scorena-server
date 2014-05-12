//
//  FLoginVC.h
//  facebet
//
//  Created by Kun on 2013-12-24.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FLoginVC : UIViewController<UITextFieldDelegate,MBProgressHUDDelegate>{
    BOOL _showKeyboard;
    NSString* _tmpUser;
    NSString* _tmpPass;
    MBProgressHUD* hud;
}

@property(nonatomic,weak) id parentVC;

IBElement UIScrollView* scrollView;

IBElement UIButton* signupBtn;
IBElement UITextField* userField;
IBElement UITextField* passField;

-(IBAction)signupClicked:(id)sender;
-(IBAction)doLogin:(id)sender;
@end
