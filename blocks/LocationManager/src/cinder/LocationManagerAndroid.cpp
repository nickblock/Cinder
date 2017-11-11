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

#include "LocationManager.h"
#include "cinder/Log.h"

#include "cinder/android/app/CinderNativeActivity.h"

namespace cinder {

class LocationManagerImplAndroid : LocationManagerImpl {

public: 
  LocationManagerImplAndroid();
  ~LocationManagerImplAndroid();
  
  virtual void            enable( float accuracyInMeters, float distanceFilter, float headingFilter );
  virtual void            disable();
  virtual bool            isEnabled() const;         
  virtual uint32_t        getErrorCountImpl() const; 
  virtual LocationEvent   getMostRecentLocation();

  static LocationManagerImplAndroid *sInst;
};

LocationManagerImplAndroid* LocationManagerImplAndroid::sInst = nullptr;

LocationManagerImpl* LocationManager::get()
{
  if( ! LocationManagerImplAndroid::sInst ) {
    
    LocationManagerImplAndroid::sInst = new LocationManagerImplAndroid();
  }
  
  return LocationManagerImplAndroid::sInst;
}

LocationManagerImplAndroid::LocationManagerImplAndroid()
{

}
LocationManagerImplAndroid::~LocationManagerImplAndroid()
{

}

void LocationManagerImplAndroid::enable( float accuracyInMeters, float distanceFilter, float headingFilter )
{
  
}

void LocationManagerImplAndroid::disable()
{
  
}

bool LocationManagerImplAndroid::isEnabled()
{

}

uint32_t LocationManagerImplAndroid::getErrorCountImpl() const
{

}

} // namespace cinder