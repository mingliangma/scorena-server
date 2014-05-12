//
//  MConstants.h
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#ifndef facebet_MConstants_h
#define facebet_MConstants_h

#define productionScorena

#ifdef productionScorena
#define kServerURL @"http://api.scorena.us"
#define kParseAppId @"sxfzjYsgGiSXVwr7pj6vmaFR2f8ok9YGrnXGfx91"
#define kParseApiKey @"IQX6dOlw7KfsLmNw2tau0cGWsE4I3vBliCw67Ca3"
#endif

typedef enum{
    kBetSelectNone=0,
    kBetSelectHome,
    kBetSelectAway
} kBetSelectType;


typedef enum{
    FBetTypeWin=0,
    FBetTypeScore
}FBetType;

typedef enum{
    //320x480
    kDeviceScreenTypeiPhone3=0,
    //640x960
    kDeviceScreenTypeiPhone4,
    //640x1136
    kDeviceScreenTypeiPhone5
} kDeviceScreenType;

typedef enum{
    kStatTimeRangeWeek,
    kStatTimeRangeMonth,
    kStatTimeRangeAll
} kStatTimeRange;


#define kRefreshUpcomingNotification @"kRefreshUpcomingNotifcation"
#define kRefreshAfterBetNotification @"kRefreshAfterBetNotifcation"
#define kRefreshBalanceNotification @"kRefreshBalanceNotifcation"

#endif
