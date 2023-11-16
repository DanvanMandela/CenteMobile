//
// Created by Danvan.Mandela on 04/04/2023.
//
#include <jni.h>
#include <string>


extern "C"
JNIEXPORT jstring JNICALL
Java_com_elmacentemobile_data_source_constants_Keys_secretKey(JNIEnv *env, jobject thiz) {
    std::string hello = "NUZDNjhDMzYyOEFGQjZFN0M4M0Q5QTQyQzg4RkQ=";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_elmacentemobile_data_source_constants_Keys_mapsKey(JNIEnv *env, jobject thiz) {
    std::string hello = "IEFJemFTeUItOEF0cWtHSkZZT3NkR3VLQmpQREFZN2Zmc3hzZkk2NA==";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_elmacentemobile_data_source_constants_Keys_ipStackKey(JNIEnv *env, jobject thiz) {
    std::string hello = "1c970564337f0c0464eb19b9f86ca859";
    return env->NewStringUTF(hello.c_str());
}