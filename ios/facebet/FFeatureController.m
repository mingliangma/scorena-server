//
//  FFeatureController.m
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FFeatureController.h"
#import "FBettingVC.h"

@interface FFeatureController ()

@end

@implementation FFeatureController

@synthesize scrollView,pageControl,features;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.title = @"Home";
        
        if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }
        [MUtil loadRightBar:self];
        games = nil;
        

    }
    return self;
}

-(void)loadPromoArray{
        [[MNetwork sharedInstance] loadPromo:self success:@selector(refreshWithDataObject:) failure:@selector(downloadFailed)];
}

-(void)downloadFailed{
    
}


-(void)refreshWithDataObject:(id)obj{
    
//    sfv(obj)
    features = NULL;
    features = obj;
    
    scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, 400+[MUtil extraHeight])];
    [scrollView setContentSize:CGSizeMake(320*[features count], 400)];
    [scrollView setPagingEnabled:YES];
    [scrollView setScrollEnabled:YES];
    [scrollView setShowsVerticalScrollIndicator:NO];
    [scrollView setDelegate:self];
    
    games = [[NSMutableArray alloc] init];
    
    [self addPromos];
    [self.view addSubview:scrollView];
}


- (void)scrollViewDidScroll:(UIScrollView *)scroll {
    CGFloat pageWidth = scroll.frame.size.width;
    float fractionalPage = scroll.contentOffset.x / pageWidth;
    NSInteger page = lround(fractionalPage);
    pageControl.currentPage = page;
}


-(void)addPromos{
    int i=0;
    
    for(i=0;i<[features count];i++){
        [self addPromo:i obj:[features objectAtIndex:i]];
    }
}

-(void)addPromo:(int)i obj:(id)obj{
    
    NSArray* nibViews = [[NSBundle mainBundle] loadNibNamed:@"FVPromo"
                                                      owner:self
                                                    options:nil];
    FVPromo* myView = [nibViews objectAtIndex: 0];
    
    FGame* game = [[FGame alloc] initWithFeatureJson:obj];
    
    [games addObject:game];
    
    [myView initWithGameObj:game];
    [myView setDelegate:self];
    [myView setFrame:CGRectMake(320*i, 0, 320, 400)];
    [scrollView addSubview:myView];
}

-(void)loadBetScreen:(id)bet{
    FBettingVC* vc = [[FBettingVC alloc] initWithNibName:@"FBettingVC" bundle:[NSBundle mainBundle]];
    [vc setMyBet:(FGame*)bet];
    [vc setupContent];
    [[self navigationController] pushViewController:vc animated:YES];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
    if(!features){
        [self loadPromoArray];
    }

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
