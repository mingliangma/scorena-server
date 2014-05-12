//
//  FCoinsVC.m
//  facebet
//
//  Created by Kyle on 2014-04-20.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FCoinsVC.h"
#import "FCoinRow.h"

@interface FCoinsVC ()

@end

@implementation FCoinsVC

@synthesize scroll;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {

        if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }
        coinArray = [FRam getIAPArray];

        [self setTitle:@"Get Coins"];
        
        self.scroll = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, 350+[MUtil extraHeight])];
        
        [self.scroll setBackgroundColor:[UIColor fVeryLightColor]];
        [self.scroll setContentSize:CGSizeMake(320, 10+[coinArray count]*95)];
        [self.view addSubview:scroll];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    for(int i=0;i<[coinArray count];i++){
        FCoinRow* row = [[FCoinRow alloc] initWithPoint:CGPointMake(10, 10+i*95)];
        id omg = [coinArray objectAtIndex:i];        
        [row configureWithObj:omg del:self];
        [self.scroll addSubview:row];
    }
}

-(void)invite{
    DD(@"Invite");
}

-(void)buyOne{
    DD(@"Buy 0.99");
}

-(void)buyTwo{
    DD(@"Buy 1.99");
}

-(void)buyThree{
    DD(@"Buy 2.99");
}

-(void)buyFour{
    DD(@"Buy 4.99");
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
 
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
