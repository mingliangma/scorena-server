//
//  MNetwork.h
//  takuya
//
//  Created by Kun on 13-09-07.
//
//

#import <Foundation/Foundation.h>

#import <SystemConfiguration/SystemConfiguration.h>
#import <MobileCoreServices/MobileCoreServices.h>
#import "AFNetworking.h"

#define SU_UserKey @"SU_UserKey"
#define SU_PassKey @"SU_PassKey"
#define SU_EmailKey @"SU_EmailKey"
#define SU_GenderKey @"SU_GenderKey"
#define SU_RegionKey @"SU_RegionKey"

#define SU_QuestionKey @"SU_QuestionKey"
#define SU_PickKey @"SU_PickKey"
#define SU_WagerKey @"SU_WagerKey"

@interface MNetwork : AFHTTPClient
+(MNetwork*) sharedInstance;


-(void)getCoins:(id)delegate
        success:(SEL)successSel
        failure:(SEL)failureSel;

-(void)refBalance:(id)delegate
        success:(SEL)successSel
        failure:(SEL)failureSel;

-(void)loadUserDetails:(id)delegate
        user:(NSString*)userToken
     success:(SEL)successSel
     failure:(SEL)failureSel;

-(void)login:(id)delegate
        user:(NSString*)user
        pass:(NSString*)pass
     success:(SEL)successSel
     failure:(SEL)failureSel;

-(void)signup:(id)delegate
         dict:(NSDictionary*)dict
      success:(SEL)successSel
      failure:(SEL)failureSel;

-(void)loadPromo:(id)delegate
         success:(SEL)successSel
         failure:(SEL)failureSel;

-(void)loadUpcoming:(id)delegate
            success:(SEL)successSel
            failure:(SEL)failureSel;

-(void)loadPastGames:(id)delegate
             success:(SEL)successSel
             failure:(SEL)failureSel;

-(void)loadQuestions:(id)delegate
             forGame:(NSString*)game
               index:(NSInteger)index
                past:(BOOL)isPast
             success:(SEL)successSel
             failure:(SEL)failureSel;

-(void)loadPayout:(id)delegate
             game:(NSString*)game_id
             q_id:(NSString*)q_id
          success:(SEL)successSel
          failure:(SEL)failureSel;

-(void)bet:(id)delegate
      dict:(NSDictionary*)dict
   success:(SEL)successSel
   failure:(SEL)failureSel;

-(void)loadGameDetail:(id)delegate
                 game:(NSString*)game
              success:(SEL)successSel
              failure:(SEL)failureSel;

-(void)loadRanking:(id)delegate
           success:(SEL)successSel
           failure:(SEL)failureSel;

-(void)resetPassword:(id)delegate
             success:(SEL)successSel
             failure:(SEL)failureSel;

@end

