//
//  FTeamButtons.m
//  facebet
//
//  Created by Kun on 2013-12-30.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FTeamButtons.h"

@implementation FTeamButtons

@synthesize homeCrowd,awayCrowd,homeWager,delegate,chooseSel;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        isPast = NO;
        homeWager = YES;
        [self loadUI];
    }
    return self;
}

-(void)setupContent:(id)obj{
    FGame* myBet = (FGame*)obj;
    [homeCrowd setTitle:[myBet pickA] forState:UIControlStateNormal];
    [awayCrowd setTitle:[myBet pickB] forState:UIControlStateNormal];
}

-(void)setupIsPast{
    isPast = YES;
    [self wagerSelect:homeCrowd];
}

-(void)loadUI{
    
    homeCrowd = [[UIButton alloc] initWithFrame:CGRectMake(28, 0, 120, 28)];
    awayCrowd = [[UIButton alloc] initWithFrame:CGRectMake(162, 0, 120, 28)];
    
    [[homeCrowd titleLabel] setFont:[UIFont fItalicFont:14]];
    [[awayCrowd titleLabel] setFont:[UIFont fItalicFont:14]];
    
    [homeCrowd setTag:7];
    [awayCrowd setTag:9];
    
    [homeCrowd addTarget:self action:@selector(wagerSelect:) forControlEvents:UIControlEventTouchUpInside];
    [awayCrowd addTarget:self action:@selector(wagerSelect:) forControlEvents:UIControlEventTouchUpInside];
    
    [homeCrowd setBackgroundColor:[UIColor whiteColor]];
    [awayCrowd setBackgroundColor:[UIColor whiteColor]];
    
    [homeCrowd setTitleColor:[UIColor fGreenColor] forState:UIControlStateNormal];
    [awayCrowd setTitleColor:[UIColor fGreenColor] forState:UIControlStateNormal];
    
    if(homeWager){
        [homeCrowd setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [homeCrowd setBackgroundColor:[UIColor fGreenColor]];
    }
    
    [[homeCrowd layer] setBorderWidth:1.0f];
    [[homeCrowd layer] setBorderColor:[UIColor fGreenColor].CGColor];
    
    [[awayCrowd layer] setBorderWidth:1.0f];
    [[awayCrowd layer] setBorderColor:[UIColor fGreenColor].CGColor];
    
    [homeCrowd setBackgroundColor:[UIColor fGreenColor]];
    [awayCrowd setBackgroundColor:[UIColor whiteColor]];
    
    [self addSubview:homeCrowd];
    [self addSubview:awayCrowd];
}

-(void)wagerSelect:(id)sender{
    UIButton* btn = (UIButton*)sender;
    if([btn tag]==7){
        [homeCrowd setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [homeCrowd setBackgroundColor:isPast?[UIColor fOrangeColor]:[UIColor fGreenColor]];
        
        [awayCrowd setTitleColor:isPast?[UIColor whiteColor]:[UIColor fGreenColor] forState:UIControlStateNormal];
        [awayCrowd setBackgroundColor:isPast?[UIColor fDarkGreen]:[UIColor whiteColor]];
        
        [delegate homePayoutSelected];
    }else if([btn tag]==9){
        [homeCrowd setTitleColor:isPast?[UIColor whiteColor]:[UIColor fGreenColor] forState:UIControlStateNormal];
        [homeCrowd setBackgroundColor:isPast?[UIColor fDarkGreen]:[UIColor whiteColor]];
        
        [awayCrowd setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [awayCrowd setBackgroundColor:isPast?[UIColor fOrangeColor]:[UIColor fGreenColor]];
        
        [delegate awayPayoutSelected];
    }
    
}

@end
