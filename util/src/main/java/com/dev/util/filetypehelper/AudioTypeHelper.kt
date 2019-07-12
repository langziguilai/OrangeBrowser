package com.dev.util.filetypehelper

fun Midi(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x4D.toByte() && buf[1] == 0x54.toByte() &&
            buf[2] == 0x68.toByte() && buf[3] == 0x64.toByte()
}

fun Mp3(buf :ByteArray) :Boolean {
    return buf.size > 2 &&
            ((buf[0] == 0x49.toByte() && buf[1] == 0x44.toByte() && buf[2] == 0x33.toByte()) ||
                    (buf[0] == 0xFF.toByte() && buf[1] == 0xfb.toByte()))
}

fun M4a(buf :ByteArray) :Boolean {
    return buf.size > 10 &&
            ((buf[4] == 0x66.toByte() && buf[5] == 0x74.toByte() && buf[6] == 0x79.toByte() &&
                    buf[7] == 0x70.toByte() && buf[8] == 0x4D.toByte() && buf[9] == 0x34.toByte() && buf[10] == 0x41.toByte()) ||
                    (buf[0] == 0x4D.toByte() && buf[1] == 0x34.toByte() && buf[2] == 0x41.toByte() && buf[3] == 0x20.toByte()))
}

fun Ogg(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x4F.toByte() && buf[1] == 0x67.toByte() &&
            buf[2] == 0x67.toByte() && buf[3] == 0x53.toByte()
}

fun Flac(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x66.toByte() && buf[1] == 0x4C.toByte() &&
            buf[2] == 0x61.toByte() && buf[3] == 0x43.toByte()
}

fun Wav(buf :ByteArray) :Boolean {
    return buf.size > 11 &&
            buf[0] == 0x52.toByte() && buf[1] == 0x49.toByte() &&
            buf[2] == 0x46.toByte() && buf[3] == 0x46.toByte() &&
            buf[8] == 0x57.toByte() && buf[9] == 0x41.toByte() &&
            buf[10] == 0x56.toByte() && buf[11] == 0x45.toByte()
}

fun Amr(buf :ByteArray) :Boolean {
    return buf.size > 11 &&
            buf[0] == 0x23.toByte() && buf[1] == 0x21.toByte() &&
            buf[2] == 0x41.toByte() && buf[3] == 0x4D.toByte() &&
            buf[4] == 0x52.toByte() && buf[5] == 0x0A.toByte()
}

fun Aac(buf :ByteArray) :Boolean {
    return buf.size > 1 &&
            ((buf[0] == 0xFF.toByte() && buf[1] == 0xF1.toByte()) ||
                    (buf[0] == 0xFF.toByte() && buf[1] == 0xF9.toByte()))
}