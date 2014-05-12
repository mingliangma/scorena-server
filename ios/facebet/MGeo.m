//
//  MGeo.m
//  movie
//
//  Created by Kun on 13-07-10.
//
//

#import "MGeo.h"

@interface MGeo ()

@end

@implementation MGeo 

static MGeo * shared = nil;

+(MGeo*) shared{
    if(shared != nil){
        return shared;
    }
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        shared = [MGeo new];
    });
    return shared;
}

-(id)init{
    if(self = [super init]){
            
        NSString* plistPath;
            
        plistPath = [[NSBundle mainBundle] pathForResource:@"geo" ofType:@"plist"];
        if(!plistPath){
        
            return nil;
        }
        
        NSDictionary* dict = [NSDictionary dictionaryWithContentsOfFile:plistPath];
        
        screenType = [MUtil screenType];
        
        switch (screenType) {
            case kDeviceScreenTypeiPhone5:
                geoMap = [dict objectForKey:@"iPhone5"];
                break;
                
            default:
                geoMap = [dict objectForKey:@"iPhone4"];
                break;
        }
        
        pointMap = [[geoMap objectForKey:@"point"] copy];
        sizeMap = [[geoMap objectForKey:@"size"] copy];
        lengthMap = [[geoMap objectForKey:@"length"] copy];
    }
    return self;
}



//Get point for key
-(CGPoint)ppk:(NSString*)key{
    NSString* str = (NSString*)[pointMap objectForKey:key];
    NSArray* arr = [str componentsSeparatedByString:@","];
    CGFloat x = [[arr objectAtIndex:0] floatValue];
    CGFloat y = [[arr objectAtIndex:1] floatValue];
    return CGPointMake(x, [MUtil screenHeight] - y);
}

//Get size for key
-(CGSize)psk:(NSString*)key{
    NSString* str = (NSString*)[sizeMap objectForKey:key];
    NSArray* arr = [str componentsSeparatedByString:@","];
    CGFloat x = [[arr objectAtIndex:0] floatValue];
    CGFloat y = [[arr objectAtIndex:1] floatValue];
    return CGSizeMake(x, y);
}

//Get length for key
-(CGFloat)plk:(NSString*)key{
    NSString* str = (NSString*)[lengthMap objectForKey:key];
    return [str floatValue];
}

@end
