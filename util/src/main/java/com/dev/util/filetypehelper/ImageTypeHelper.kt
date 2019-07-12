package com.dev.util.filetypehelper

fun Jpeg(buf :ByteArray) :Boolean {
    return buf.size > 2 &&
            buf[0] == 0xFF.toByte() &&
            buf[1] == 0xD8.toByte() &&
            buf[2] == 0xFF.toByte()
}

fun Jpeg2000(buf :ByteArray) :Boolean {
    return buf.size > 12 &&
            buf[0] == 0x0.toByte() &&
            buf[1] == 0x0.toByte() &&
            buf[2] == 0x0.toByte() &&
            buf[3] == 0xC.toByte() &&
            buf[4] == 0x6A.toByte() &&
            buf[5] == 0x50.toByte() &&
            buf[6] == 0x20.toByte() &&
            buf[7] == 0x20.toByte() &&
            buf[8] == 0xD.toByte() &&
            buf[9] == 0xA.toByte() &&
            buf[10] == 0x87.toByte() &&
            buf[11] == 0xA.toByte() &&
            buf[12] == 0x0.toByte()
}

fun Png(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x89.toByte() && buf[1] == 0x50.toByte() &&
            buf[2] == 0x4E.toByte() && buf[3] == 0x47.toByte()
}

fun Gif(buf :ByteArray) :Boolean {
    return buf.size > 2 &&
            buf[0] == 0x47.toByte() && buf[1] == 0x49.toByte() && buf[2] == 0x46.toByte()
}

fun Webp(buf :ByteArray) :Boolean {
    return buf.size > 11 &&
            buf[8] == 0x57.toByte() && buf[9] == 0x45.toByte() &&
            buf[10] == 0x42.toByte() && buf[11] == 0x50.toByte()
}

fun CR2(buf :ByteArray) :Boolean {
    return buf.size > 9 &&
            ((buf[0] == 0x49.toByte() && buf[1] == 0x49.toByte() && buf[2] == 0x2A.toByte() && buf[3] == 0x0.toByte()) ||
                    (buf[0] == 0x4D.toByte() && buf[1] == 0x4D.toByte() && buf[2] == 0x0.toByte() && buf[3] == 0x2A.toByte())) &&
            buf[8] == 0x43.toByte() && buf[9] == 0x52.toByte()
}

fun Tiff(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            ((buf[0] == 0x49.toByte() && buf[1] == 0x49.toByte() && buf[2] == 0x2A.toByte() && buf[3] == 0x0.toByte()) ||
                    (buf[0] == 0x4D.toByte() && buf[1] == 0x4D.toByte() && buf[2] == 0x0.toByte() && buf[3] == 0x2A.toByte()))
}

fun Bmp(buf :ByteArray) :Boolean {
    return buf.size > 1 &&
            buf[0] == 0x42.toByte() &&
            buf[1] == 0x4D.toByte()
}

fun Jxr(buf :ByteArray) :Boolean {
    return buf.size > 2 &&
            buf[0] == 0x49.toByte() &&
            buf[1] == 0x49.toByte() &&
            buf[2] == 0xBC.toByte()
}

fun Psd(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x38.toByte() && buf[1] == 0x42.toByte() &&
            buf[2] == 0x50.toByte() && buf[3] == 0x53.toByte()
}

fun Ico(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x00.toByte() && buf[1] == 0x00.toByte() &&
            buf[2] == 0x01.toByte() && buf[3] == 0x00.toByte()
}


fun Dwg(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x41.toByte() && buf[1] == 0x43.toByte() &&
            buf[2] == 0x31.toByte() && buf[3] == 0x30.toByte()
}