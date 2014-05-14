//
//  FFaceController.h
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FAccountController.h"
#import "FUserStatVC.h"
#import "FLoginVC.h"

/**
    The user account controller that's the base view controller for the user account tab
 */

@interface FFaceController : UINavigationController{
    FAccountController * account;
    FUserStatVC* stat;
    FLoginVC* login;
}


-(void)switchToLogin;
-(void)switchToSignUp;
-(void)switchToProfile:(id)obj;
-(void)switchToStat:(id)obj;

@end
