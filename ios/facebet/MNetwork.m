//
//  MNetwork.m
//  takuya
//
//  Created by Kun on 13-09-07.
//
//

#import "MNetwork.h"

@interface MNetwork ()

@end

@implementation MNetwork


static MNetwork * sharedInstance = nil;

+(MNetwork*) sharedInstance{
    if(sharedInstance != nil){
        return sharedInstance;
    }
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [MNetwork new];
    });
    return sharedInstance;
}

-(id)init{
    if(self = [super initWithBaseURL:[NSURL URLWithString:kServerURL]]){
        [self setAllowsInvalidSSLCertificate:YES];    
    }
    return self;
}

-(NSMutableURLRequest *)requestWithMethod:(NSString *)method
                                      path:(NSString *)path
                                parameters:(NSDictionary *)parameters{
    return [super requestWithMethod:method path:[NSString stringWithFormat:@"%@",path] parameters:nil];
}




-(void)loadUserDetails:(id)delegate
        user:(NSString*)userToken
     success:(SEL)successSel
     failure:(SEL)failureSel{
    
    NSMutableURLRequest *request = [self requestWithMethod:@"GET" path:[NSString stringWithFormat:@"v1/users/%@",userToken] parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){
                                                                          if([[operation response] statusCode]==200){
                                                                              DD(@"User Detail Retrieve Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{
                                                                              DD(@"User Detail Retrieve Not 200");
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){
                                                                          DD(@"User Detail Retrieve Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:error.localizedDescription];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}


-(void)getCoins:(id)delegate
             success:(SEL)successSel
             failure:(SEL)failureSel{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:[(UIViewController*)delegate navigationController].view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.removeFromSuperViewOnHide = YES;
    
    NSMutableURLRequest *request = [self requestWithMethod:@"GET" path:[NSString stringWithFormat:@"v1/users/%@/getCoins",[[FGlobal sharedInstance] userId]] parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    [header setObject:@"application/json" forKey:@"Content-Type"];
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){
                                                                          [hud hide:YES];
                                                                          if([[operation response] statusCode]==200){
                                                                              
                                                                              
                                                                              DD(@"Get Coins Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{
                                                                              DD(@"Get Coins Not 200");
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){
                                                                          [hud hide:YES];
                                                                          DD(@"Get Coins Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:error.localizedDescription];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}




-(void)refBalance:(id)delegate
        success:(SEL)successSel
        failure:(SEL)failureSel{
    
   // MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:[(UIViewController*)delegate navigationController].view animated:YES];
  //  hud.mode = MBProgressHUDModeIndeterminate;
  //  hud.removeFromSuperViewOnHide = YES;
    
    NSMutableURLRequest *request = [self requestWithMethod:@"GET" path:[NSString stringWithFormat:@"v1/users/%@/balance",[[FGlobal sharedInstance] userId]] parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    [header setObject:@"application/json" forKey:@"Content-Type"];
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){
                                                                         // [hud hide:YES];
                                                                          if([[operation response] statusCode]==200){
                                                                              
                                                                              
                                                                              DD(@"Refresh Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{
                                                                              DD(@"Refresh Balance Not 200");
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){
                                                                        //  [hud hide:YES];
                                                                          DD(@"Refresh Balance Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:error.localizedDescription];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}




-(void)resetPassword:(id)delegate
               success:(SEL)successSel
               failure:(SEL)failureSel{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:[(UIViewController*)delegate navigationController].view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.removeFromSuperViewOnHide = YES;
    
    NSMutableURLRequest *request = [self requestWithMethod:@"POST" path:@"v1/users/requestPasswordReset" parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
        [header setObject:@"application/json" forKey:@"Content-Type"];
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    
    NSString* str = [NSString stringWithFormat:@"{'email':'%@'}",[[[FGlobal sharedInstance] credential] objectForKey:@"email"]];
    [request setHTTPBody:[str dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:NO]];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){
                                                                              [hud hide:YES];
                                                                          if([[operation response] statusCode]==200){

                                                                              
                                                                              DD(@"User Password Reset Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{
                                                                              DD(@"User Password Reset Not 200");
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){
                                                                              [hud hide:YES];
                                                                          DD(@"User password reset Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:error.localizedDescription];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}



-(void)login:(id)delegate
        user:(NSString*)user
        pass:(NSString*)pass
      success:(SEL)successSel
      failure:(SEL)failureSel{
    
    
    NSMutableURLRequest *request = [self requestWithMethod:@"GET" path:[NSString stringWithFormat:@"v1/login?username=%@&password=%@",user,pass] parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){
                                                                          if([[operation response] statusCode]==200){
                                                                              DD(@"Login Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{
                                                                              DD(@"Login Status Not 200");
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){

                                                                          DD(@"Login Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:error.localizedDescription];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}



-(void)signup:(id)delegate
         dict:(NSDictionary*)dict
             success:(SEL)successSel
             failure:(SEL)failureSel{
    
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:[(UIViewController*)delegate navigationController].view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.removeFromSuperViewOnHide = YES;
    
    NSMutableURLRequest *request = [self requestWithMethod:@"POST" path:@"v1/users/new" parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    
    [header setObject:@"application/json" forKey:@"Content-Type"];

    
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    NSData* data = [[dict singupString] dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:NO];
    [request setHTTPBody:data];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){
                                                                          [hud hide:YES];
                                                                          if([[operation response] statusCode]==200||[[operation response] statusCode]==201){
                                                                              DD(@"Signup Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{

                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                             
                                                                              if([delegate respondsToSelector:failureSel]){
                                                                                  [delegate performSelector:failureSel withObject:[obj objectForKey:@"error"]];
                                                                              }
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){
                                                                          [hud hide:YES];
                                                                          DD(@"Signup Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:error.localizedDescription];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}

-(void)loadPromo:(id)delegate
           success:(SEL)successSel
           failure:(SEL)failureSel{
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:[(UIViewController*)delegate navigationController].view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.removeFromSuperViewOnHide = YES;
    
    NSMutableURLRequest *request = [self requestWithMethod:@"GET" path:[NSString stringWithFormat:@"v1/sports/soccer/leagues/all/games/feature?userId=%@",[[FGlobal sharedInstance] userId]] parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){
                                                                          [hud hide:YES];
                                                                          if([[operation response] statusCode]==200){
                                                                              DD(@"Promo Download Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{
                                                                              DD(@"Promo Download Status Not 200");
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){
                                                                          [hud hide:YES];
                                                                          DD(@"Promo Download Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:nil];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}

-(NSString*)getUserPath{
    NSString* userPath;
    if([[FGlobal sharedInstance] authenticated]){
        userPath = [NSString stringWithFormat:@"?userId=%@",[[FGlobal sharedInstance] userId]];
    }else{
        userPath = @"";
    }
    return userPath;
}

-(void)loadUpcoming:(id)delegate
         success:(SEL)successSel
         failure:(SEL)failureSel{

    
    NSMutableURLRequest *request = [self requestWithMethod:@"GET" path:[NSString stringWithFormat:@"v1/sports/soccer/leagues/all/games/upcoming%@",[self getUserPath]] parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){
                                                                          if([[operation response] statusCode]==200){
                                                                              DD(@"Upcoming Game Download Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{
                                                                              DD(@"Upcoming Game Download Status Not 200");
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){

                                                                          DD(@"Upcoming Download Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:nil];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}



-(void)loadPastGames:(id)delegate
            success:(SEL)successSel
            failure:(SEL)failureSel{
    
    
    NSMutableURLRequest *request = [self requestWithMethod:@"GET" path:[NSString stringWithFormat:@"v1/sports/soccer/leagues/all/games/past%@",[self getUserPath]] parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){
                                                                          if([[operation response] statusCode]==200){
                                                                              DD(@"Past Game Download Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{
                                                                              DD(@"Past Game Download Status Not 200");
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){
                                                                          DD(@"Past Download Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:nil];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}


-(void)loadGameDetail:(id)delegate
                 game:(NSString*)game
              success:(SEL)successSel
              failure:(SEL)failureSel{
    
    NSMutableURLRequest *request = [self requestWithMethod:@"GET" path:[NSString stringWithFormat:@"v1/sports/soccer/leagues/all/games/%@",game] parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){

                                                                          if([[operation response] statusCode]==200){
                                                                              DD(@"Game Detail Download Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              
                                                                              
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{
                                                                              DD(@"Game Detail Status Not 200");
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){

                                                                          DD(@"Game Detail Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:nil];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
    
    
    
}
//http://scorenat-env.elasticbeanstalk.com/v1/sports/ranking?userId=MO3lF6iZ8B


-(void)loadRanking:(id)delegate
         success:(SEL)successSel
         failure:(SEL)failureSel{
    
    NSMutableURLRequest *request = [self requestWithMethod:@"GET" path:[NSString stringWithFormat:@"v1/sports/ranking?userId=%@",[[FGlobal sharedInstance] userId]] parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){
                                                                          if([[operation response] statusCode]==200){
                                                                              DD(@"Ranking Download Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{
                                                                              DD(@"Ranking Download Status Not 200");
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){

                                                                          DD(@"Ranking Download Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:nil];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}


-(void)loadQuestions:(id)delegate
             forGame:(NSString*)game
               index:(NSInteger)index
                past:(BOOL)isPast
             success:(SEL)successSel
             failure:(SEL)failureSel{
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:[(UIViewController*)delegate navigationController].view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.removeFromSuperViewOnHide = YES;
    
    NSMutableURLRequest *request = [self requestWithMethod:@"GET" path:[NSString stringWithFormat:@"v1/sports/soccer/leagues/all/games/%@/qs?userId=%@",game,[[FGlobal sharedInstance] userId]] parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    DD(@"Load Question For Game:%@ , userId: %@",game,[[FGlobal sharedInstance] userId]);
    
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){
                                                                          [hud hide:YES];
                                                                          if([[operation response] statusCode]==200){
                                                                              DD(@"Game Questions Download Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              
                                                                              NSDictionary* dict = [NSDictionary dictionaryWithObjectsAndKeys:obj,@"json",[NSNumber numberWithInteger:index],@"index",[NSNumber numberWithBool:isPast],@"past",nil];
                                                                              
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:dict];
                                                                              }
                                                                          }else{
                                                                              DD(@"Game Download Status Not 200");
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){
                                                                          [hud hide:YES];
                                                                          DD(@"Game Download Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:nil];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}


-(void)loadPayout:(id)delegate
             game:(NSString*)game_id
             q_id:(NSString*)q_id
             success:(SEL)successSel
             failure:(SEL)failureSel{
    
    NSMutableURLRequest *request = [self requestWithMethod:@"GET" path:[NSString stringWithFormat:@"v1/sports/soccer/leagues/all/games/%@/qs/%@?userId=%@",game_id,q_id,[[FGlobal sharedInstance] userId]] parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){

                                                                          if([[operation response] statusCode]==200){
                                                                              DD(@"Past Game Download Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{
                                                                              DD(@"Past Game Download Status Not 200");
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){

                                                                          DD(@"Past Download Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:nil];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}


-(void)bet:(id)delegate
         dict:(NSDictionary*)dict
      success:(SEL)successSel
      failure:(SEL)failureSel{
    MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:[(UIViewController*)delegate navigationController].view animated:YES];
    hud.mode = MBProgressHUDModeIndeterminate;
    hud.removeFromSuperViewOnHide = YES;
    
    NSMutableURLRequest *request = [self requestWithMethod:@"POST" path:@"v1/sports/soccer/leagues/all/wagers/new" parameters:nil];
    NSMutableDictionary* header = [[NSMutableDictionary alloc] init];
    
    [header setObject:@"application/json" forKey:@"Content-Type"];
    
    [request setTimeoutInterval:15];
    [request setCachePolicy:NSURLRequestUseProtocolCachePolicy];
    [request setAllHTTPHeaderFields:header];
    NSData* data = [[dict betString] dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:NO];
    [request setHTTPBody:data];
    
    DD(@"%@",[dict betString]);
    
    AFHTTPRequestOperation *operation = [self HTTPRequestOperationWithRequest:request
                                                                      success:^(AFHTTPRequestOperation *operation, NSData* responseData){
                                                                          [hud hide:YES];
                                                                          if([[operation response] statusCode]==200||[[operation response] statusCode]==201){
                                                                              DD(@"Bet Success");
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              if([delegate respondsToSelector:successSel]){
                                                                                  [delegate performSelector:successSel withObject:obj];
                                                                              }
                                                                          }else{
                                                                              
                                                                              NSError* err;
                                                                              id obj = [NSJSONSerialization JSONObjectWithData:responseData options:NSJSONReadingMutableLeaves error:&err];
                                                                              
                                                                              if([delegate respondsToSelector:failureSel]){
                                                                                  [delegate performSelector:failureSel withObject:[obj objectForKey:@"error"]];
                                                                              }
                                                                          }
                                                                      }
                                                                      failure:^(AFHTTPRequestOperation *operation, NSError *error){
                                                                          [hud hide:YES];
                                                                          DD(@"Bet Failed: %@",error);
                                                                          if([delegate respondsToSelector:failureSel]){
                                                                              [delegate performSelector:failureSel withObject:[[error userInfo] objectForKey:@"NSLocalizedRecoverySuggestion"]];
                                                                          }
                                                                      }];
    [self enqueueHTTPRequestOperation:operation];
}




@end
