//
//  FMoreTableController.m
//  facebet
//
//  Created by Kun on 2013-12-20.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FMoreTableController.h"
#import "FHistoryVC.h"
#import "FBankVC.h"
#import "FAboutVC.h"
#import "FCharts.h"
#import "FCoinsVC.h"

@interface FMoreTableController ()

@end

@implementation FMoreTableController


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.title = @"More";
        
        [self.view setBackgroundColor:[UIColor fGreenColor]];
        
        if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }
    }
    return self;
}

-(void)viewDidAppear:(BOOL)animated{
    [super viewDidAppear:animated];
}


-(IBAction)loadAbout:(id)sender{
    FAboutVC* vc = [[FAboutVC alloc] initWithNibName:@"FAboutVC" bundle:[NSBundle mainBundle]];
    [self.navigationController pushViewController:vc animated:YES];
}


-(IBAction)loadBank:(id)sender{
    if([[FGlobal sharedInstance] authenticated]){
        FBankVC* vc = [[FBankVC alloc] initWithNibName:@"FBankVC" bundle:[NSBundle mainBundle]];
        [self.navigationController pushViewController:vc animated:YES];
    }else{
        [MUtil showAlert:@"Please login first" del:self];
    }
}

-(IBAction)loadRank:(id)sender{
    if([[FGlobal sharedInstance] authenticated]){
        FHistoryVC* vc = [[FHistoryVC alloc] initWithNibName:@"FHistoryVC" bundle:[NSBundle mainBundle]];
        [self.navigationController pushViewController:vc animated:YES];
    }else{
        [MUtil showAlert:@"Please login first" del:self];
    }
}


-(IBAction)loadCoins:(id)sender{
    FCoinsVC* vc = [[FCoinsVC alloc] initWithNibName:@"FCoinsVC" bundle:[NSBundle mainBundle]];
    [self.navigationController pushViewController:vc animated:YES];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
