/*
 Copyright (c) 2015, The Cinder Project

 This code is intended to be used with the Cinder C++ library, http://libcinder.org

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

#include "cinder/app/android/AssetFileSystem.h"
#include "cinder/app/android/EventManagerAndroid.h"
#include <cstdio>

namespace cinder { namespace app { namespace android {

AAssetManager *GetNativeAssetManager()
{
	AAssetManager *assetMan = EventManagerAndroid::instance()->getNativeApp()->activity->assetManager;
	return assetMan;
}

bool AssetFileSystem_exists( ci::fs::path path )
{
	bool result = false;
	if( nullptr != EventManagerAndroid::instance() ) {
		AAssetManager *assetMan = GetNativeAssetManager();
		AAsset *asset = AAssetManager_open( assetMan, path.string().c_str(), AASSET_MODE_UNKNOWN );
		if( nullptr != asset ) {
			result = true;
			AAsset_close( asset );
		}
	}
	return result;
}

AssetFileSystem_FILE * AssetFileSystem_fopen( const char * filename, const char * mode )
{
	AAssetManager * assetMan = GetNativeAssetManager();
	AssetFileSystem_FILE * result = AAssetManager_open( assetMan, filename, AASSET_MODE_UNKNOWN );
	return result;
}

int AssetFileSystem_fclose( AssetFileSystem_FILE * stream )
{
	AAsset_close( stream );
	return 0;
}

int AssetFileSystem_fseek( AssetFileSystem_FILE * stream, long int offset, int origin )
{
	return AAsset_seek( stream, offset, origin );
}

long int AssetFileSystem_ftell( AssetFileSystem_FILE * stream )
{
	off_t total = AAsset_getLength( stream );
	off_t remain = AAsset_getRemainingLength( stream );
	off_t offset = total - remain;
	return (offset >= 0 && offset <= total) ? offset : -1L;
}

size_t AssetFileSystem_fread( void * ptr, size_t size, size_t count, AssetFileSystem_FILE * stream )
{
	size_t n = size*count;
	return AAsset_read( stream, ptr, n );
}

int AssetFileSystem_feof( AssetFileSystem_FILE * stream )
{
	int n = AAsset_getRemainingLength( stream );
	return (0 == n) ? 1 : 0;
}

long int AssetFileSystem_flength( AssetFileSystem_FILE * stream )
{
	long int n = AAsset_getLength( stream );
	return n;
}


static int android_read(void* cookie, char* buf, int size) {
  return AAsset_read((AAsset*)cookie, buf, size);
}

static int android_write(void* cookie, const char* buf, int size) {
  return EACCES; // can't provide write access to the apk
}

static fpos_t android_seek(void* cookie, fpos_t offset, int whence) {
  return AAsset_seek((AAsset*)cookie, offset, whence);
}

static int android_close(void* cookie) {
  AAsset_close((AAsset*)cookie);
  return 0;
}

#ifdef __cplusplus
extern "C" {

FILE* android_fopen(const char* fname, const char* mode) {

  if(mode[0] == 'w') return NULL;

	AAssetManager * assetMan = GetNativeAssetManager();
  AAsset* asset = AAssetManager_open(assetMan, fname, 0);
  if(!asset) return NULL;

  return funopen(asset, android_read, android_write, android_seek, android_close);
}
}
#endif
} } } // namespace cinder::app