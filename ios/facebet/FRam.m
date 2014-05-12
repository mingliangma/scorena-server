//
//  FRam.m
//  facebet
//
//  Created by Kyle on 2014-04-25.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FRam.h"


@implementation FRam

+(NSMutableArray*)getIAPArray{
    NSMutableArray* array = [[NSMutableArray alloc] init];
    
    [array addObject:[FRam dictWithObjects:@"iap_1.png"
                                      coin:@"100"
                                      desc:@"Invite your friends from facebook to get coins"
                                    button:@"Invite"
                                     price:@"10"
                                       sel:@"invite"]];
    
    [array addObject:[FRam dictWithObjects:@"iap_1.png"
                                      coin:@"100"
                                      desc:@""
                                    button:@"Buy"
                                     price:@"0.99"
                                        sel:@"buyOne"]];
    
    [array addObject:[FRam dictWithObjects:@"iap_2.png"
                                      coin:@"250"
                                      desc:@""
                                    button:@"Buy"
                                     price:@"1.99"
                                       sel:@"buyTwo"]];
    
    [array addObject:[FRam dictWithObjects:@"iap_3.png"
                                      coin:@"500"
                                      desc:@""
                                    button:@"Buy"
                                     price:@"2.99"
                                       sel:@"buyThree"]];
    
    [array addObject:[FRam dictWithObjects:@"iap_4.png"
                                      coin:@"1000"
                                      desc:@""
                                    button:@"Buy"
                                     price:@"4.99"
                                    sel:@"buyFour"]];                      
    
    return array;
}

+(NSDictionary*)dictWithObjects:(NSString*)pic coin:(NSString*)coin desc:(NSString*)desc button:(NSString*)button price:(NSString*)price sel:(NSString*)sel{
    return [NSDictionary dictionaryWithObjectsAndKeys:pic,IAPImageKey,coin,IAPCoinKey,desc,IAPDescKey,button,IAPBuyKey,price,IAPPriceKey,sel,IAPSelKey,nil];
}


@end
