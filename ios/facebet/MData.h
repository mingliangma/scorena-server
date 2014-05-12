//
//  MData.h
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "MDb.h"

@interface MData : NSObject


+(MData*) sharedInstance;

-(NSArray*)getPromoArray;
-(NSArray*)getGamesArray;

-(NSArray*)getHomePayoutArray;
-(NSArray*)getAwayPayoutArray;

-(FCrowd*)tempCrowd;

@end