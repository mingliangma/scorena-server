//
//  FTeam.h
//  facebet
//
//  Created by Kun on 2013-12-20.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FTeam : NSObject<NSCopying>

@property(nonatomic,strong) NSString* imageName;
@property(nonatomic,strong) NSString* teamName;
@property(nonatomic,assign) NSInteger score;

-(id)initWithJson:(id)obj;
+(NSInteger)getScoreWithTeamJson:(id)obj;

@end
