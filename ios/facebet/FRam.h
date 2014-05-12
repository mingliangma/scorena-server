//
//  FRam.h
//  facebet
//
//  Created by Kyle on 2014-04-25.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


#define IAPImageKey @"IAPImageKey"
#define IAPCoinKey @"IAPCoinKey"
#define IAPDescKey @"IAPDescKey"
#define IAPPriceKey @"IAPPriceKey"
#define IAPBuyKey @"IAPBuyKey"
#define IAPSelKey @"IAPSelKey"

@interface FRam : NSObject

+(NSMutableArray*)getIAPArray;

@end
