//
//  FTPastCell.m
//  facebet
//
//  Created by Kun on 2013-12-29.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FTPastCell.h"

@implementation FTPastCell

@synthesize betButtons,overBtn;

@synthesize myGame,path,parent,adjustSEL;

@synthesize gameTypeHead,isOpen;


- (id)init{
    self = [super initWithFrame:CGRectMake(0, 0, 300, 65)];
    if (self) {
        // Initialization code
       
    }
    return self;
}


-(void)setupGame:(id)obj handler:(id<FTeamButtonProtocol>)handle{
    myGame = (FGame*)obj;
    [gameTypeHead setupContent:obj];
    [betButtons setupGame:obj past:YES];
}

-(void)initPastCell{
    
    gameTypeHead = [[FUGameType alloc] initWithFrame:CGRectMake(0, 5, 320, 60)];
    [self addSubview:gameTypeHead];
    
    overBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 320, 60)];
    [overBtn addTarget:self action:@selector(toggleDetail) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:overBtn];
    
    if(isOpen){
        betButtons = [[FBetButtons alloc] initWithFrame:CGRectMake(0, 47, 300, 120)];
     //   [betButtons setupGame:myGame];
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
