//
//  FReloadView.m
//  facebet
//
//  Created by Kyle on 2014-04-26.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FReloadView.h"

@implementation FReloadView

@synthesize spin;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"strips.png"]]];
   // [self setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"Scorena_Stripe.png"]]];

        spin = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"load_pie.png"]];
        [spin setCenter:CGPointMake(160, 90)];
        [self addSubview:spin];
        
    }
    return self;
}





-(void)triggerUpdate{
    sfv(@"Start spin")

    [spin spinForever];
}

-(void)startSpin{
    sfv(@"Start 22spin")
    [spin spinForever];
}

-(void)stopSpin{
    [spin stopSpin];
}


@end
