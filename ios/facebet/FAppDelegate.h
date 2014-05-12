//
//  FAppDelegate.h
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FTabController.h"

@interface FAppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) IBOutlet FTabController* tabController;
@property (strong, nonatomic) UIWindow *window;

@end
