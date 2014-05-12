//
//  FWagerScroll.m
//  facebet
//
//  Created by Kun on 2013-12-28.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FWagerScroll.h"

#define kWaterOffset 45

@implementation FWagerScroll

@synthesize price,del,sel;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        [self setBackgroundColor:[UIColor clearColor]];
        [self setContentSize:CGSizeMake(380, 48)];
        [self setShowsHorizontalScrollIndicator:NO];
        price = 0;
        [self loadButtons];

    }
    return self;
}

-(void)selectNewBetWager:(NSInteger)wager{
    price = wager;
    [self buttonPressed:[self viewWithTag:price]];
}

-(void)selectAlreadyPlacedWager:(NSInteger)wager{
    
    for(int i=0;i<[[self subviews] count];i++){
        id obj = [[self subviews] objectAtIndex:i];
        
        if([obj isKindOfClass:[UIButton class]]){
            UIButton* btn2 = (UIButton*)obj;
            [btn2 setTitleColor:[UIColor fLightGrayColor] forState:UIControlStateNormal];
        }
    }
    
    UIButton* btn = (UIButton*)[self viewWithTag:wager];
    [self scrollRectToVisible:btn.frame animated:NO];
    [self setUserInteractionEnabled:NO];
    
    if([btn isKindOfClass:[UIButton class]]){
        [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        [btn setBackgroundImage:[UIImage imageNamed:@"Gray_Crown.png"] forState:UIControlStateNormal];
    }
}


-(void)loadButtons{
    
    NSArray* wagers = [MUtil wagerArray];
    
    NSUInteger index=0;
    for(index = 0;index<[wagers count];index++){
        
        NSString* ii = (NSString*)[wagers objectAtIndex:index];

        UIButton* btn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 45, 45)];
        [[btn titleLabel] setFont:[UIFont boldSystemFontOfSize:20]];
        [btn setTitle:[NSString stringWithFormat:@"%@",[ii copy]] forState:UIControlStateNormal];
        [btn setTitleColor:[UIColor fGreenColor] forState:UIControlStateNormal];
        [btn setTag:[ii integerValue]];
        [btn addTarget:self action:@selector(buttonPressed:) forControlEvents:UIControlEventTouchUpInside];

        [btn shift:10+index*kWaterOffset y:0];
        if([self isSelected:ii]){
            [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
            [btn setBackgroundImage:[UIImage imageNamed:@"Red_Crown.png"] forState:UIControlStateNormal];
        }
        
        [self addSubview:btn];
    }
}

-(void)configDelegate:(id)delegate selector:(SEL)selector{
    del = delegate;
    sel = selector;
    
}

-(BOOL)isSelected:(NSString*)str{
    return price == [str integerValue];
}

-(void)buttonPressed:(id)sender{
    UIButton* btn = (UIButton*)sender;
    NSInteger cost = btn.tag;
    
    UIButton* oldBtn = (UIButton*)[self viewWithTag:price];
    [oldBtn setTitleColor:[UIColor fGreenColor] forState:UIControlStateNormal];
    [oldBtn setBackgroundImage:nil forState:UIControlStateNormal];
    
    [btn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [btn setBackgroundImage:[UIImage imageNamed:@"Red_Crown.png"] forState:UIControlStateNormal];
   
    price = cost;
    
    if([del respondsToSelector:sel]){
        [del performSelector:sel withObject:[NSNumber numberWithInteger:cost] afterDelay:0];
    }
}



@end
