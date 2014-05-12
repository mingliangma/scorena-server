//
//  FWager.h
//  facebet
//
//  Created by Kun on 2013-12-28.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FWager : NSObject

@property(nonatomic,assign) NSInteger amount;
@property(nonatomic,strong) NSString* name;
@property(nonatomic,assign) NSInteger payout;

-(id)initWithJson:(id)obj;

@end
