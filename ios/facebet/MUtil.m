//
//  MUtil.m
//  takuya
//
//  Created by Kun on 13-02-17.
//
//

#import "MUtil.h"
#import "FBalance.h"

@implementation MUtil

+(void)showAlert:(NSString*)msg del:(id)del{
    [MUtil showAlert:msg title:@"Error" del:del];
}

+(void)showAlert:(NSString*)msg title:(NSString*)title del:(id)del{
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:title message:msg delegate:del cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}

+(NSArray*)wagerArray{
    NSArray* array = [NSArray arrayWithObjects:@"5",@"10",@"15",@"20",@"25",@"50",@"75",@"100",nil];
    return array;
}

+(NSString *)appDocumentsDir:(NSString*)file{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *basePath = ([paths count] > 0) ? [paths objectAtIndex:0] : nil;
    return [basePath stringByAppendingFormat:@"/%@",file];
}

+(NSString*)tauntFromType:(NSString*)type{
    if([type isEqualToString:@"win"]){
        return @"WHO WILL\nWIN BETWEEN\nTHE TWO?";
    }else if([type isEqualToString:@"score"]){
        return @"WHO WILL\nSCORE MORE?";
    }else{
        return @"";
    }
}

+(NSString*)gameTypeString:(NSString*)type{
    return [NSString stringWithFormat:@"%@",type];
}


+(void)loadRightBar:(UIViewController*)vc{
    
    FBalance* bal = [[FBalance alloc] initWithFrame:CGRectMake(0, 0, 80, 44)];
    [bal setVc:vc];
    UIBarButtonItem* coinButton = [[UIBarButtonItem alloc] initWithCustomView:bal];
    
    vc.navigationItem.rightBarButtonItem = coinButton;
}

+(kDeviceScreenType)screenType{
    
    if(UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone){
        CGSize result = [[UIScreen mainScreen] bounds].size;
        if(result.height == 480){
            if ([[UIScreen mainScreen] respondsToSelector:@selector(scale)] == YES && [[UIScreen mainScreen] scale] == 2.00) {
                return kDeviceScreenTypeiPhone4;
            }else{
                return kDeviceScreenTypeiPhone3;
            }
        }else if(result.height == 568){
            return kDeviceScreenTypeiPhone5;
        }
    }
    return kDeviceScreenTypeiPhone5;
    
}

+(CGFloat)screenHeight{
    CGSize result = [[UIScreen mainScreen] bounds].size;
    return result.height;
}

+(CGFloat)extraHeight{
    if([MUtil screenType] == kDeviceScreenTypeiPhone5){
        return 88;
    }
    return 0;
}


@end
