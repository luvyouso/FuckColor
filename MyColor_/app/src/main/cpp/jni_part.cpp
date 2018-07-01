#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include "filters.h"

extern "C" {

JNIEXPORT void JNICALL Java_coloring_org_jp_ktcc_full_util_UtilNative_sketchPencil(JNIEnv* env, jobject, jlong addrSrc, jlong addrDst, jint blurRadius, jint contrast)
{
	sketchPencil(*(Mat*)addrSrc, *(Mat*)addrDst, (int)blurRadius, (int)contrast);
}

JNIEXPORT void JNICALL Java_coloring_org_jp_ktcc_full_util_UtilNative_colorCartoonFilter(JNIEnv* env, jobject, jlong addrSrc, jlong addrDst, jint thickness, jint threshold)
{
	colorCartoonFilter(*(Mat*)addrSrc, *(Mat*)addrDst, (int)thickness, (int)threshold);
}

JNIEXPORT void JNICALL Java_coloring_org_jp_ktcc_full_util_UtilNative_grayCartoonFilter(JNIEnv* env, jobject, jlong addrSrc, jlong addrDst, jint thickness, jint threshold)
{
	grayCartoonFilter(*(Mat*)addrSrc, *(Mat*)addrDst, (int)thickness, (int)threshold);
}

JNIEXPORT void JNICALL Java_coloring_org_jp_ktcc_full_util_UtilNative_oilPaintFilter(JNIEnv* env, jobject, jlong addrSrc, jlong addrDst, jint radius, jint levels)
{
	oilPaintFilter(*(Mat*)addrSrc, *(Mat*)addrDst, (int)radius, (int)levels);
}

JNIEXPORT void Java_coloring_org_jp_ktcc_full_util_UtilNative_waterColorFilter(JNIEnv* env, jobject, jlong addrSrc, jlong addrDst, jint spatialRadius, jint colorRadius, jint maxLevels, jint scaleFactor)
{
	waterColorFilter(*(Mat*)addrSrc, *(Mat*)addrDst, (int)spatialRadius, (int)colorRadius, (int)maxLevels, (int)scaleFactor);
}

JNIEXPORT void JNICALL Java_coloring_org_jp_ktcc_full_util_UtilNative_setSketchTexture(JNIEnv* env, jobject, jlong sketchTexture)
{ 
	SketchFilter* sketchFilter =  SketchFilter::getInstance();
	sketchFilter->setSketchTexture(*(Mat*)sketchTexture);
}

JNIEXPORT void JNICALL Java_coloring_org_jp_ktcc_full_util_UtilNative_pencilSketchFilter(JNIEnv* env, jobject, jlong addrSrc, jlong addrDst, jint sketchBlend, jint contrast)
{ 
	SketchFilter* sketchFilter =  SketchFilter::getInstance();
	sketchFilter->applyPencilSketch(*(Mat*)addrSrc, *(Mat*)addrDst, (int)sketchBlend, (int)contrast);
}

JNIEXPORT void JNICALL Java_coloring_org_jp_ktcc_full_util_UtilNative_colorSketchFilter(JNIEnv* env, jobject, jlong addrSrc, jlong addrDst, jint sketchBlend, jint contrast)
{ 
	SketchFilter* sketchFilter =  SketchFilter::getInstance();
	sketchFilter->applyColorSketch(*(Mat*)addrSrc, *(Mat*)addrDst, (int)sketchBlend, (int)contrast);
}

}
