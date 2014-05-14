//
//  FGlobal.h
//  facebet
//
//  Created by Kyle on 2014-01-03.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
    Global object that holds the user information
    All global information persists on the device by writing to and reading from NSUserDefaults
 */

#define FUserKey @"F_UserKey"
#define FGenderKey @"F_GenderKey"
#define FRegionKey @"F_RegionKey"
#define FEmailKey @"F_EmailKey"
#define FUserNameKey @"F_UserNameKey"
#define FSessionToken @"F_SessionToken"
#define FCredential @"F_Credential"
#define FBalanceKey @"currentBalance"

@interface FGlobal : NSObject

+(FGlobal*) sharedInstance;

@property(nonatomic,strong) FGame* current;

@property(nonatomic,strong) NSString* sessionToken;
@property(nonatomic,strong) NSString* userId;
@property(nonatomic,strong) NSString* gender;
@property(nonatomic,strong) NSString* region;
@property(nonatomic,strong) NSString* email;
@property(nonatomic,strong) NSString* userName;
@property(nonatomic,assign) NSInteger balance;

@property(nonatomic,strong) id credential;

-(BOOL)authenticated;
-(void)logout;

-(void)reduceBalance:(NSInteger)bal;

@end