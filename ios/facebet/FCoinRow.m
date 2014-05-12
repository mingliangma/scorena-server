//
//  FCoinRow.m
//  facebet
//
//  Created by Kyle on 2014-04-25.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FCoinRow.h"

@implementation FCoinRow

@synthesize coinImg,coinLB,buyBtn,priceLB,descBtn,delegate,sel;

-(id)initWithPoint:(CGPoint)point{
    
    NSArray* array =  [[NSBundle mainBundle] loadNibNamed:@"FCoinRow" owner:nil options:nil];
    self = [array objectAtIndex:0];
    [self setFrame:CGRectMake(point.x, point.y, 300, 88)];
    
    return self;
}

-(void)awakeFromNib{
    [buyBtn.layer setBackgroundColor:[UIColor fRedColor].CGColor];
    [buyBtn.layer setShadowColor:[UIColor blackColor].CGColor];
    [buyBtn.layer setShadowRadius:5.0];
    [buyBtn.layer setShadowOffset:CGSizeMake(0.0, 3.0)];
    [buyBtn.layer setMasksToBounds:NO];

    [[buyBtn titleLabel] setTextAlignment:NSTextAlignmentCenter];
    [self.layer setShadowColor:[UIColor blackColor].CGColor];
    [self.layer setShadowOffset:CGSizeMake(0.0, 3.0)];
    [self.layer setShadowRadius:5.0];
    [self.layer setMasksToBounds:NO];
}

-(void)configureWithObj:(id)obj del:(id)del{
    [coinImg setImage:[UIImage imageNamed:[obj objectForKey:IAPImageKey]]];
    [priceLB setText:[obj objectForKey:IAPPriceKey]];
    [coinLB setText:[obj objectForKey:IAPCoinKey]];
    [descBtn setText:[obj objectForKey:IAPDescKey]];
    [buyBtn setTitle:[obj objectForKey:IAPBuyKey] forState:UIControlStateNormal];
    [buyBtn setTitleColor:[UIColor fRedColor] forState:UIControlStateHighlighted];
    [buyBtn addTarget:del action:NSSelectorFromString([obj objectForKey:IAPSelKey]) forControlEvents:UIControlEventTouchUpInside];
}


@end
