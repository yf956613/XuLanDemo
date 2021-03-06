/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_android_rfid245G_SerialPort */

#ifndef _Included_com_android_rfid245G_SerialPort
#define _Included_com_android_rfid245G_SerialPort
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_android_rfid245G_SerialPort
 * Method:    open
 * Signature: (II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_android_rfid245G_SerialPort_open
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     com_android_rfid245G_SerialPort
 * Method:    close
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_android_rfid245G_SerialPort_close
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_android_rfid245G_SerialPort
 * Method:    rfidPoweron
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_android_rfid245G_SerialPort_rfidPoweron
  (JNIEnv *, jobject);

/*
 * Class:     com_android_rfid245G_SerialPort
 * Method:    rfidPoweroff
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_android_rfid245G_SerialPort_rfidPoweroff
  (JNIEnv *, jobject);

/*
 * Class:     com_android_rfid245G_SerialPort
 * Method:    psamPowerOn
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_android_rfid245G_SerialPort_psamPowerOn
  (JNIEnv *, jobject);

/*
 * Class:     com_android_rfid245G_SerialPort
 * Method:    psamPoweroff
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_android_rfid245G_SerialPort_psamPoweroff
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif
