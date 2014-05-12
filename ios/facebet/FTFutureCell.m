//
//  FTFutureCell.m
//  facebet
//
//  Created by Kun on 2013-12-29.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FTFutureCell.h"

#define kBaseX -300
#define kBaseY -50

@implementation FTFutureCell

@synthesize betButtons,overBtn;

@synthesize myGame,isOpen,path,parent,adjustSEL;

@synthesize gameTypeHead;

-(id)init{
    self = [super initWithFrame:CGRectMake(0, 0, 300, 65)];
    if(self){
    }
    return self;
}

-(void)setupGame:(FGame*)game handler:(id<FBetButtonProtocol>)handle{
    myGame = game;

    [gameTypeHead setupContent:myGame];
    
    [betButtons setDelegate:handle];
    [betButtons setupGame:myGame past:NO];
}

-(void)initFutureCell{
    
    gameTypeHead = [[FUGameType alloc] initWithFrame:CGRectMake(0, 5, 320, 60)];
    [self addSubview:gameTypeHead];
    
    overBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 320, 60)];
    [overBtn addTarget:self action:@selector(toggleDetail) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:overBtn];
    
    if(isOpen){
        betButtons = [[FBetButtons alloc] initWithFrame:CGRectMake(0, 40, 300, 120)];
        [self insertSubview:betButtons belowSubview:gameTypeHead];
        [self setFrame:CGRectMake(0, 0, 300, 140)];
    }
}

-(void)toggleDetail{
    if(!isOpen){
        isOpen = YES;
    }else{
        isOpen = NO;
    }
        [parent performSelector:adjustSEL withObject:path afterDelay:0];
}


@end
