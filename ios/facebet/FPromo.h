//
//  FPromo.h
//  facebet
//
//  Created by Kun on 2013-12-17.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface FPromo : NSObject

@property(nonatomic,strong) NSDate* gameTime;
@property(nonatomic,assign) NSString* type;
@property(nonatomic,strong) FTeam* teamA;
@property(nonatomic,strong) FTeam* teamB;

-(id)initWithJson:(id)obj;

@end


/*
 
 "promo":[{"id":"1",
 "date":"12-17-2013",
 "type":"win",
 "teamA":{"name":"Chelsea",
 "image":"chelsea.jpg"},
 "teamB":{"name":"Man United",
 "image":"manunited.jpg"}},
 
 */