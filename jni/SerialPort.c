/*
 * Copyright 2009-2011 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>

#include "devapi.h"
#include "SerialPort.h"

#include "android/log.h"
static const char *TAG="serial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)


/*
 * Class:     com_android_rfid245G_SerialPort
 * Method:    open
 * Signature: (II)Ljava/io/FileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_com_android_rfid245G_SerialPort_open
  (JNIEnv *env, jclass thiz, jint port, jint baudrate)
{
	int fd;
	speed_t speed;
	jobject mFileDescriptor;


	/* Opening device */
	{
	  fd = h900_uart_open(port, baudrate);
		if (fd == -1)
		{
			/* Throw an exception */
//			LOGE("Cannot open port");
			/* TODO: throw an exception */
			return NULL;
		}
	}

	/* Create a corresponding file descriptor */
	{
		jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");
		jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V");
		jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
		mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
		(*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint)fd);
	}
//	LOGD("open(fd = %d)", fd);
	return mFileDescriptor;
}

/*
 * Class:     com_android_rfid245G_SerialPort
 * Method:    close
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_android_rfid245G_SerialPort_close
  (JNIEnv *env, jobject thiz, jint port)
{
	jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
	jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

	jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
	jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

	jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
	jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

//	LOGD("close(fd = %d)", descriptor);
	h900_uart_close(port, descriptor);
}

/*
 * Class:     com_android_rfid245G_SerialPort
 * Method:    MCUpoweron
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_android_rfid245G_SerialPort_rfidPoweron
  (JNIEnv *env, jobject thiz)
{
	h900_ex3v3_power_on();
	h900_rfid_power_on();
}
/*
 * Class:     com_android_rfid245G_SerialPort
 * Method:    MCUpoweroff
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_android_rfid245G_SerialPort_rfidPoweroff
  (JNIEnv *env, jobject thiz)
{
	h900_ex3v3_power_off();
	h900_rfid_power_off();
}

JNIEXPORT void JNICALL Java_com_android_rfid245G_SerialPort_psamPowerOn
(JNIEnv *env, jobject thiz)
{
	h900_psam_power_on();
}

JNIEXPORT void JNICALL Java_com_android_rfid245G_SerialPort_psamPoweroff
(JNIEnv *env, jobject thiz)
{
	h900_psam_power_off();
}
