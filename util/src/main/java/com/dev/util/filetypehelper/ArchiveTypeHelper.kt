package com.dev.util.filetypehelper


const val ARCHIVE_MAX_BYTE=300

fun Zip(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x50.toByte() && buf[1] == 0x4B.toByte() &&
            (buf[2] == 0x3.toByte() || buf[2] == 0x5.toByte() || buf[2] == 0x7.toByte()) &&
            (buf[3] == 0x4.toByte() || buf[3] == 0x6.toByte() || buf[3] == 0x8.toByte())
}

fun Tar(buf :ByteArray) :Boolean {
    return buf.size > 261 &&
            buf[257] == 0x75.toByte() && buf[258] == 0x73.toByte() &&
            buf[259] == 0x74.toByte() && buf[260] == 0x61.toByte() &&
            buf[261] == 0x72.toByte()
}

fun Rar(buf :ByteArray) :Boolean {
    return buf.size > 6 &&
            buf[0] == 0x52.toByte() && buf[1] == 0x61.toByte() && buf[2] == 0x72.toByte() &&
            buf[3] == 0x21.toByte() && buf[4] == 0x1A.toByte() && buf[5] == 0x7.toByte() &&
            (buf[6] == 0x0.toByte() || buf[6] == 0x1.toByte())
}

fun Gz(buf :ByteArray) :Boolean {
    return buf.size > 2 &&
            buf[0] == 0x1F.toByte() && buf[1] == 0x8B.toByte() && buf[2] == 0x8.toByte()
}

fun Bz2(buf :ByteArray) :Boolean {
    return buf.size > 2 &&
            buf[0] == 0x42.toByte() && buf[1] == 0x5A.toByte() && buf[2] == 0x68.toByte()
}

fun SevenZ(buf :ByteArray) :Boolean {
    return buf.size > 5 &&
            buf[0] == 0x37.toByte() && buf[1] == 0x7A.toByte() && buf[2] == 0xBC.toByte() &&
            buf[3] == 0xAF.toByte() && buf[4] == 0x27.toByte() && buf[5] == 0x1C.toByte()
}



fun Exe(buf :ByteArray) :Boolean {
    return buf.size > 1 &&
            buf[0] == 0x4D.toByte() && buf[1] == 0x5A.toByte()
}

fun Swf(buf :ByteArray) :Boolean {
    return buf.size > 2 &&
            (buf[0] == 0x43.toByte() || buf[0] == 0x46.toByte()) &&
            buf[1] == 0x57.toByte() && buf[2] == 0x53.toByte()
}

fun Rtf(buf :ByteArray) :Boolean {
    return buf.size > 4 &&
            buf[0] == 0x7B.toByte() && buf[1] == 0x5C.toByte() &&
            buf[2] == 0x72.toByte() && buf[3] == 0x74.toByte() &&
            buf[4] == 0x66.toByte()
}

fun Nes(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x4E.toByte() && buf[1] == 0x45.toByte() &&
            buf[2] == 0x53.toByte() && buf[3] == 0x1A.toByte()
}

fun Crx(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x43.toByte() && buf[1] == 0x72.toByte() &&
            buf[2] == 0x32.toByte() && buf[3] == 0x34.toByte()
}

fun Cab(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            ((buf[0] == 0x4D.toByte() && buf[1] == 0x53.toByte() && buf[2] == 0x43.toByte() && buf[3] == 0x46.toByte()) ||
                    (buf[0] == 0x49.toByte() && buf[1] == 0x53.toByte() && buf[2] == 0x63.toByte() && buf[3] == 0x28.toByte()))
}

fun Eot(buf :ByteArray) :Boolean {
    return buf.size > 35 &&
            buf[34] == 0x4C.toByte() && buf[35] == 0x50.toByte() &&
            ((buf[8] == 0x02.toByte() && buf[9] == 0x00.toByte() &&
                    buf[10] == 0x01.toByte()) || (buf[8] == 0x01.toByte() &&
                    buf[9] == 0x00.toByte() && buf[10] == 0x00.toByte()) ||
                    (buf[8] == 0x02.toByte() && buf[9] == 0x00.toByte() &&
                            buf[10] == 0x02.toByte()))
}

fun Ps(buf :ByteArray) :Boolean {
    return buf.size > 1 &&
            buf[0] == 0x25.toByte() && buf[1] == 0x21.toByte()
}

fun Xz(buf :ByteArray) :Boolean {
    return buf.size > 5 &&
            buf[0] == 0xFD.toByte() && buf[1] == 0x37.toByte() &&
            buf[2] == 0x7A.toByte() && buf[3] == 0x58.toByte() &&
            buf[4] == 0x5A.toByte() && buf[5] == 0x00.toByte()
}

fun Sqlite(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x53.toByte() && buf[1] == 0x51.toByte() &&
            buf[2] == 0x4C.toByte() && buf[3] == 0x69.toByte()
}

fun Deb(buf :ByteArray) :Boolean {
    return buf.size > 20 &&
            buf[0] == 0x21.toByte() && buf[1] == 0x3C.toByte() && buf[2] == 0x61.toByte() &&
            buf[3] == 0x72.toByte() && buf[4] == 0x63.toByte() && buf[5] == 0x68.toByte() &&
            buf[6] == 0x3E.toByte() && buf[7] == 0x0A.toByte() && buf[8] == 0x64.toByte() &&
            buf[9] == 0x65.toByte() && buf[10] == 0x62.toByte() && buf[11] == 0x69.toByte() &&
            buf[12] == 0x61.toByte() && buf[13] == 0x6E.toByte() && buf[14] == 0x2D.toByte() &&
            buf[15] == 0x62.toByte() && buf[16] == 0x69.toByte() && buf[17] == 0x6E.toByte() &&
            buf[18] == 0x61.toByte() && buf[19] == 0x72.toByte() && buf[20] == 0x79.toByte()
}

fun Ar(buf :ByteArray) :Boolean {
    return buf.size > 6 &&
            buf[0] == 0x21.toByte() && buf[1] == 0x3C.toByte() &&
            buf[2] == 0x61.toByte() && buf[3] == 0x72.toByte() &&
            buf[4] == 0x63.toByte() && buf[5] == 0x68.toByte() &&
            buf[6] == 0x3E.toByte()
}

fun Z(buf :ByteArray) :Boolean {
    return buf.size > 1 &&
            ((buf[0] == 0x1F.toByte() && buf[1] == 0xA0.toByte()) ||
                    (buf[0] == 0x1F.toByte() && buf[1] == 0x9D.toByte()))
}

fun Lz(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x4C.toByte() && buf[1] == 0x5A.toByte() &&
            buf[2] == 0x49.toByte() && buf[3] == 0x50.toByte()
}

fun Rpm(buf :ByteArray) :Boolean {
    return buf.size > 96 &&
            buf[0] == 0xED.toByte() && buf[1] == 0xAB.toByte() &&
            buf[2] == 0xEE.toByte() && buf[3] == 0xDB.toByte()
}

fun Elf(buf :ByteArray) :Boolean {
    return buf.size > 52 &&
            buf[0] == 0x7F.toByte() && buf[1] == 0x45.toByte() &&
            buf[2] == 0x4C.toByte() && buf[3] == 0x46.toByte()
}

fun Dcm(buf :ByteArray) :Boolean {
    return buf.size > 131 &&
            buf[128] == 0x44.toByte() && buf[129] == 0x49.toByte() &&
            buf[130] == 0x43.toByte() && buf[131] == 0x4D.toByte()
}