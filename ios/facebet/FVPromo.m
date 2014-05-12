//
//  FVPromo.m
//  facebet
//
//  Created by Kun on 2013-12-17.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FVPromo.h"
#import "FBettingVC.h"

@implementation FVPromo

@synthesize teamA,teamB,dateLB,betBtn,taunt,delegate,bottomA,bottomB;

-(void)initWithFeature:(id)feature{

    [teamA setText:[[myPromo teamA] teamName]];
    [teamB setText:[[myPromo teamB] teamName]];
    [dateLB setText:[NSString stringWithFormat:@"- %@ -",[[myPromo gameTime] monthDateYear]]];
    [taunt setText:[[MUtil tauntFromType:[myPromo type]] uppercaseString] ];
    [taunt setFont:[UIFont fStraightFont:28]];
    [taunt setTextColor:[UIColor whiteColor]];
    [taunt setTextAlignment:NSTextAlignmentCenter];
}

-(void)initWithGameObj:(FGame*)obj{
    myPromo = obj;
    [teamA setText:[obj teamA].teamName];
    [teamB setText:[obj teamB].teamName];
    [bottomA setText:[obj teamA].teamName];
    [bottomB setText:[obj teamB].teamName];
    
    [dateLB setText:[[obj gameTime] monthDateYear]];
    
    [taunt setText:[[obj type] uppercaseString]];
    [taunt setFont:[UIFont fStraightFont:38]];
    [taunt setTextColor:[UIColor whiteColor]];
    [taunt setTextAlignment:NSTextAlignmentCenter];
    
}


-(void)initWithNetworkData:(id)feature{
    [teamA setText:[feature objectForKey:@"home"] ];
    [teamB setText:[feature objectForKey:@"away"]];
    
    [bottomA setText:[feature objectForKey:@"home"]];
    [bottomB setText:[feature objectForKey:@"away"]];
    
    NSString* time = [feature objectForKey:@"date"];
    
    [dateLB setText:[NSString stringWithFormat:@"- %@ -",[[time dateObjectFromNetwork] monthDateYear]]];

    id obj = [feature objectForKey:@"question"];
    [taunt setText:[[obj objectForKey:@"content"] uppercaseString]];
    [taunt setFont:[UIFont fStraightFont:28]];
    [taunt setTextColor:[UIColor whiteColor]];
    [taunt setTextAlignment:NSTextAlignmentCenter];
    
    DD(@"Network Data: %@",feature);
}

-(IBAction)betClicked:(id)sender{
    [delegate performSelector:NSSelectorFromString(@"loadBetScreen:") withObject:myPromo afterDelay:0];
}

@end
