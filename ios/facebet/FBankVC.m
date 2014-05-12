//
//  FBankVC.m
//  facebet
//
//  Created by Kun on 2013-12-20.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FBankVC.h"
#import "FCoinsVC.h"

@interface FBankVC ()

@end

@implementation FBankVC

@synthesize balanceLB,getBtn;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        
                    self.navigationItem.backBarButtonItem.tintColor = [UIColor whiteColor];
        // Custom initialization
        if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }
        [self setTitle:@"My Bank"];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refresh) name:kRefreshBalanceNotification object:nil];

    [self refresh];
    [self refreshBalance];
}

- (void) dealloc{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
}

-(void)refresh{
    
    [self configureBalance:[[FGlobal sharedInstance] balance]];
}




-(void) refreshBalance

{
    
    [[MNetwork sharedInstance] refBalance:self success:@selector(refBalSuccess:) failure:@selector(reBalFailed:)];
    
    
    
}

-(void)refBalSuccess:(id)obj{
    
    if(obj){
        [[FGlobal sharedInstance] setBalance:[[obj objectForKey:@"currentBalance"] integerValue]];
        
        NSLog(@"CurrentBalance: %@",[obj objectForKey:@"currentBalance"] );
        //    [self configureBalance:[[FGlobal sharedInstance] balance]];
    }
    
    /*
     *  Show the get coins UI here after a successful retrieval
     */
    
    
}





-(void) refBalFailed
{
    NSLog(@"Refresh Balance Fail");
}


-(void)configureBalance:(NSInteger)balance{
    [balanceLB setText:[NSString stringWithFormat:@"%d",balance]];
    /*
    if(balance >50){
        [getBtn setEnabled:NO];
    }else{
        [getBtn setEnabled:YES];
    }
     */
}


-(void)getCoinSuccess:(id)obj{
    
    if(obj){
        [[FGlobal sharedInstance] setBalance:[[obj objectForKey:@"currentBalance"] integerValue]];
        [self configureBalance:[[FGlobal sharedInstance] balance]];
    }
    
    /*
     *  Show the get coins UI here after a successful retrieval
     */
    
    
}

-(void)getCoinFailed:(id)obj{
    
    [MUtil showAlert:@"Failed to get coins, please try again later" del:self];
}


-(IBAction)getCoins:(id)sender{
    /*
    FCoinsVC* vc = [[FCoinsVC alloc] initWithNibName:@"FCoinsVC" bundle:[NSBundle mainBundle]];
    [self.navigationController pushViewController:vc animated:YES];
    
     */
    
    if([[FGlobal sharedInstance] balance] >50){
        [MUtil showAlert:@"Balance above 50 cannot get coins" del:self];
    }else{
        [[MNetwork sharedInstance] getCoins:self success:@selector(getCoinSuccess:) failure:@selector(getCoinFailed:)];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
