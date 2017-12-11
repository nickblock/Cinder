/*
 Copyright (c) 2012, The Cinder Project, All rights reserved.

 This code is intended for use with the Cinder C++ library: http://libcinder.org
 
 Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this list of conditions and
  the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
  the following disclaimer in the documentation and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
*/

#pragma once

#include "cinder/Cinder.h"
#include "cinder/Signals.h"
#include "cinder/Vector.h"

namespace cinder {
    
//! Represents a location event
class LocationEvent {

public:

  LocationEvent( double lon = 0.f, double lat = 0.f, float speed = 0, float altitude = 0,
           float horizontalAccuracy = 0, float verticalAccuracy = 0 )
    : mAltitude( altitude ), mLongitude(lon), mLatitude(lat), mHorizontalAccuracy( horizontalAccuracy ),
      mSpeed( speed ), mVerticalAccuracy( verticalAccuracy )
  {
  }
  
  //! Returns the altitude in meters of the location event
  float     getAltitude() const { return mAltitude; }
  //! Returns the latitude coordinate of the location event
  float     getLatitude() const { return mLatitude; }
  //! Returns the latitude coordinate of the location event
  float     getLongitude() const { return mLongitude; }
  //! Returns speed in meters of location event
  float     getSpeed() const { return mSpeed; }
  //! Returns the horizontal accuracy of location event -- invalid when negative
  float     getHorizontalAccuracy() const { return mHorizontalAccuracy; }
  //! Returns the vertical accuracy of location event -- invalid when negative
  float     getVerticalAccuracy() const { return mVerticalAccuracy; }
  
private:

  float     mAltitude;
  double    mLatitude;
  double    mLongitude;
  float     mSpeed;
  float     mVerticalAccuracy;
  float     mHorizontalAccuracy;
};

typedef signals::Signal<void (const LocationEvent&)>  EventSignalLocation;


#if defined( CINDER_COCOA_TOUCH )
//! Represents a heading event
class HeadingEvent {
  public: 
  HeadingEvent( float magneticHeading = 0, float trueHeading = 0, float headingAccuracy = 0, 
         const std::string &description = "", const vec3 &data = vec3( 0 ) )
    : mData( data ), mDescription( description ), mHeadingAccuracy( headingAccuracy ), 
      mMagneticHeading( magneticHeading ), mTrueHeading( trueHeading )
  {}
  
  //! Returns raw geo magnetism vector from heading event
  vec3          getData() const { return mData; }
  //! Returns description of heading event
  std::string   getDescription() const { return mDescription; }
  //! Returns heading accuracy of heading event
  float         getHeadingAccuracy() const { return mHeadingAccuracy; }
  //! Returns magnetic heading in radians of the heading event
  float         getMagneticHeading() const { return mMagneticHeading; }
  //! Returns true heading in radians of heading event
  float         getTrueHeading() const { return mTrueHeading; }
  
  private:  
  vec3      mData;
  std::string   mDescription;
  float     mHeadingAccuracy;
  float     mMagneticHeading;
  float     mTrueHeading;
  
};

typedef signals::Signal<void (const HeadingEvent&)>   EventSignalHeading;

#endif

class LocationManagerImpl {

  public:

  LocationManagerImpl() {};
  
  virtual void            enable( float accuracyInMeters, float distanceFilter, float headingFilter ) = 0;
  virtual void            disable()                                                                   = 0;
  virtual bool            isEnabled() const                                                           = 0;
  virtual uint32_t        getErrorCount() const                                                       = 0;
  virtual LocationEvent   getMostRecentLocation() { return mLocation; }

  EventSignalLocation     mSignalLocationChanged;
#if defined( CINDER_COCOA_TOUCH )
  EventSignalHeading      mSignalHeadingChanged;
#endif

  LocationEvent   mLocation;
};

class LocationManager {

public:
  static void   enable( float accuracyInMeters = 20, float distanceFilter = 0, float headingFilter = 0.1f ) {
    get()->enable(accuracyInMeters, distanceFilter, headingFilter);
  }
  static void   disable() {
    get()->disable();
  }
  static bool   isEnabled() {
    return get()->isEnabled();
  }
  
  static EventSignalLocation& getSignalLocationChanged() { return get()->mSignalLocationChanged; }
  static void                 emitLocationChanged( const LocationEvent &event ) {

   get()->mLocation = event;
   get()->mSignalLocationChanged.emit( event ); 
 }

#if defined( CINDER_COCOA_TOUCH )
  static EventSignalHeading&  getSignalHeadingChanged() { return get()->mSignalHeadingChanged; }
  static void                 emitHeadingChanged( const HeadingEvent &event ) { get()->mSignalHeadingChanged.emit( event ); }
#endif

  static LocationEvent        getMostRecentLocation() { return get()->getMostRecentLocation(); }

  //! Returns the number of errors Location Services have reported
  static uint32_t             getErrorCount() { return get()->getErrorCount(); }

protected:

  static LocationManagerImpl* get(); //this wants defining in platform implementation
  
};
  
} // namespace cinder
