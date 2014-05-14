//
//  FAppDelegate.m
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FAppDelegate.h"

#import "FHomeController.h"
#import "FBetsController.h"
#import "FFaceController.h"
#import "FMorecontroller.h"

@implementation FAppDelegate

@synthesize tabController;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    
    [Parse setApplicationId:kParseAppId
                  clientKey:kParseApiKey];
    
    [PFAnalytics trackAppOpenedWithLaunchOptions:launchOptions];
    
   // [PFFacebookUtils initializeFacebook];
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    // Override point for customization after application launch.
    self.window.backgroundColor = [UIColor whiteColor];
    self.tabController   = [[FTabController alloc] initWithNibName:@"FTabController" bundle:[NSBundle mainBundle]];
    
    [self initTabBarControllers];
    
    self.window.rootViewController = self.tabController;
    [self.window makeKeyAndVisible];    
    
    return YES;
}

-(void)initTabBarControllers{
    FHomeController *home = [[FHomeController alloc] initWithNibName:@"FHomeController" bundle:[NSBundle mainBundle]];
    
    FBetsController* bet = [[FBetsController alloc] initWithNibName:@"FBetsController" bundle:[NSBundle mainBundle]];
    
    FFaceController* face = [[FFaceController alloc] initWithNibName:@"FFaceController" bundle:[NSBundle mainBundle]];
    
    FMoreController* more = [[FMoreController alloc] initWithNibName:@"FMoreController" bundle:[NSBundle mainBundle]];
    
    NSMutableArray* controllers = [[NSMutableArray alloc] init];
    [controllers addObject:home];
    [controllers addObject:bet];
    [controllers addObject:face];
    [controllers addObject:more];
    self.tabController.viewControllers = controllers;
    [self customizeTabBar];
}

-(void)customizeTabBar{
    
    UITabBar* tabBar = self.tabController.tabBar;
    
    UITabBarItem* tabItem1 = [tabBar.items objectAtIndex:0];
    UITabBarItem* tabItem2 = [tabBar.items objectAtIndex:1];
    UITabBarItem* tabItem3 = [tabBar.items objectAtIndex:2];
    UITabBarItem* tabItem4 = [tabBar.items objectAtIndex:3];
    
    tabItem1.title = @"Home";
    tabItem2.title = @"Games";
    tabItem3.title = @"Profile";
    tabItem4.title = @"More";
    
    
    //The tab bar images at the bottom
    [tabItem1 setFinishedSelectedImage:[UIImage imageNamed:@"Icon_Tab_Home_Down.png"] withFinishedUnselectedImage:[UIImage imageNamed:@"Icon_Tab_Home_Up.png"]];
    [tabItem2 setFinishedSelectedImage:[UIImage imageNamed:@"Icon_Tab_Game_Down.png"] withFinishedUnselectedImage:[UIImage imageNamed:@"Icon_Tab_Game_Up.png"]];
    [tabItem3 setFinishedSelectedImage:[UIImage imageNamed:@"Icon_Tab_Profile_Down.png"] withFinishedUnselectedImage:[UIImage imageNamed:@"Icon_Tab_Profile_Up.png"]];
    [tabItem4 setFinishedSelectedImage:[UIImage imageNamed:@"Icon_Tab_More_Down.png"] withFinishedUnselectedImage:[UIImage imageNamed:@"Icon_Tab_More_Up.png"]];
    

    [tabBar setBackgroundImage:[UIImage imageNamed:@"Bar_Tab.png"]];
    
    [tabItem1 setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], UITextAttributeTextColor, nil] forState:UIControlStateNormal];
    [tabItem2 setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], UITextAttributeTextColor, nil] forState:UIControlStateNormal];
    [tabItem3 setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], UITextAttributeTextColor, nil] forState:UIControlStateNormal];
    [tabItem4 setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], UITextAttributeTextColor, nil] forState:UIControlStateNormal];
    
    [tabItem1 setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], UITextAttributeTextColor, nil] forState:UIControlStateSelected];
    [tabItem2 setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], UITextAttributeTextColor, nil] forState:UIControlStateSelected];
    [tabItem3 setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], UITextAttributeTextColor, nil] forState:UIControlStateSelected];
    [tabItem4 setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIColor whiteColor], UITextAttributeTextColor, nil] forState:UIControlStateSelected];
    
    tabItem1.titlePositionAdjustment = UIOffsetMake(0.0, -7.0);
    tabItem2.titlePositionAdjustment = UIOffsetMake(0.0, -7.0);
    tabItem3.titlePositionAdjustment = UIOffsetMake(0.0, -7.0);
    tabItem4.titlePositionAdjustment = UIOffsetMake(0.0, -7.0);
    
    
    
      [tabBar setFrame:CGRectMake(tabBar.frame.origin.x, tabBar.frame.origin.y, tabBar.frame.size.width,tabBar.frame.size.height)];
    
//    [tabBar setFrame:CGRectMake(tabBar.frame.origin.x, tabBar.frame.origin.y-20, tabBar.frame.size.width,tabBar.frame.size.height+20)];
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
