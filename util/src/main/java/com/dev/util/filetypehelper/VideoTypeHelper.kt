package com.dev.util.filetypehelper

fun M4v(buf:ByteArray) :Boolean {
    return buf.size > 10 &&
            buf[4] == 0x66.toByte() && buf[5] == 0x74.toByte() &&
            buf[6] == 0x79.toByte() && buf[7] == 0x70.toByte() &&
            buf[8] == 0x4D.toByte() && buf[9] == 0x34.toByte() &&
            buf[10] == 0x56.toByte()
}

fun Mkv(buf:ByteArray) :Boolean {
    return (buf.size > 15 &&
            buf[0] == 0x1A.toByte() && buf[1] == 0x45.toByte() &&
            buf[2] == 0xDF.toByte() && buf[3] == 0xA3.toByte() &&
            buf[4] == 0x93.toByte() && buf[5] == 0x42.toByte() &&
            buf[6] == 0x82.toByte() && buf[7] == 0x88.toByte() &&
            buf[8] == 0x6D.toByte() && buf[9] == 0x61.toByte() &&
            buf[10] == 0x74.toByte() && buf[11] == 0x72.toByte() &&
            buf[12] == 0x6F.toByte() && buf[13] == 0x73.toByte() &&
            buf[14] == 0x6B.toByte() && buf[15] == 0x61.toByte()) ||
            (buf.size > 38 &&
                    buf[31] == 0x6D.toByte() && buf[32] == 0x61.toByte() &&
                    buf[33] == 0x74.toByte() && buf[34] == 0x72.toByte() &&
                    buf[35] == 0x6f.toByte() && buf[36] == 0x73.toByte() &&
                    buf[37] == 0x6B.toByte() && buf[38] == 0x61.toByte())
}

fun Webm(buf:ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x1A.toByte() && buf[1] == 0x45.toByte() &&
            buf[2] == 0xDF.toByte() && buf[3] == 0xA3.toByte()
}

fun Mov(buf:ByteArray) :Boolean {
    return buf.size > 15 && ((buf[0] == 0x0.toByte() && buf[1] == 0x0.toByte() &&
            buf[2] == 0x0.toByte() && buf[3] == 0x14.toByte() &&
            buf[4] == 0x66.toByte() && buf[5] == 0x74.toByte() &&
            buf[6] == 0x79.toByte() && buf[7] == 0x70.toByte()) ||
            (buf[4] == 0x6d.toByte() && buf[5] == 0x6f.toByte() && buf[6] == 0x6f.toByte() && buf[7] == 0x76.toByte()) ||
            (buf[4] == 0x6d.toByte() && buf[5] == 0x64.toByte() && buf[6] == 0x61.toByte() && buf[7] == 0x74.toByte()) ||
            (buf[12] == 0x6d.toByte() && buf[13] == 0x64.toByte() && buf[14] == 0x61.toByte() && buf[15] == 0x74.toByte()))
}

fun Avi(buf:ByteArray) :Boolean {
    return buf.size > 10 &&
            buf[0] == 0x52.toByte() && buf[1] == 0x49.toByte() &&
            buf[2] == 0x46.toByte() && buf[3] == 0x46.toByte() &&
            buf[8] == 0x41.toByte() && buf[9] == 0x56.toByte() &&
            buf[10] == 0x49.toByte()
}

fun Wmv(buf:ByteArray) :Boolean {
    return buf.size > 9 &&
            buf[0] == 0x30.toByte() && buf[1] == 0x26.toByte() &&
            buf[2] == 0xB2.toByte() && buf[3] == 0x75.toByte() &&
            buf[4] == 0x8E.toByte() && buf[5] == 0x66.toByte() &&
            buf[6] == 0xCF.toByte() && buf[7] == 0x11.toByte() &&
            buf[8] == 0xA6.toByte() && buf[9] == 0xD9.toByte()
}

fun Mpeg(buf:ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x0.toByte() && buf[1] == 0x0.toByte() &&
            buf[2] == 0x1.toByte() && buf[3] >= 0xb0.toByte() &&
            buf[3] <= 0xbf.toByte()
}

fun Flv(buf:ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x46.toByte() && buf[1] == 0x4C.toByte() &&
            buf[2] == 0x56.toByte() && buf[3] == 0x01.toByte()
}

fun Mp4(buf:ByteArray) :Boolean {
    return buf.size > 11 &&
            (buf[4] == 'f'.toByte() && buf[5] == 't'.toByte() && buf[6] == 'y'.toByte() && buf[7] == 'p'.toByte()) &&
            ((buf[8] == 'a'.toByte() && buf[9] == 'v'.toByte() && buf[10] == 'c'.toByte() && buf[11] == '1'.toByte()) ||
                    (buf[8] == 'd'.toByte() && buf[9] == 'a'.toByte() && buf[10] == 's'.toByte() && buf[11] == 'h'.toByte()) ||
                    (buf[8] == 'i'.toByte() && buf[9] == 's'.toByte() && buf[10] == 'o'.toByte() && buf[11] == '2'.toByte()) ||
                    (buf[8] == 'i'.toByte() && buf[9] == 's'.toByte() && buf[10] == 'o'.toByte() && buf[11] == '3'.toByte()) ||
                    (buf[8] == 'i'.toByte() && buf[9] == 's'.toByte() && buf[10] == 'o'.toByte() && buf[11] == '4'.toByte()) ||
                    (buf[8] == 'i'.toByte() && buf[9] == 's'.toByte() && buf[10] == 'o'.toByte() && buf[11] == '5'.toByte()) ||
                    (buf[8] == 'i'.toByte() && buf[9] == 's'.toByte() && buf[10] == 'o'.toByte() && buf[11] == '6'.toByte()) ||
                    (buf[8] == 'i'.toByte() && buf[9] == 's'.toByte() && buf[10] == 'o'.toByte() && buf[11] == 'm'.toByte()) ||
                    (buf[8] == 'm'.toByte() && buf[9] == 'm'.toByte() && buf[10] == 'p'.toByte() && buf[11] == '4'.toByte()) ||
                    (buf[8] == 'm'.toByte() && buf[9] == 'p'.toByte() && buf[10] == '4'.toByte() && buf[11] == '1'.toByte()) ||
                    (buf[8] == 'm'.toByte() && buf[9] == 'p'.toByte() && buf[10] == '4'.toByte() && buf[11] == '2'.toByte()) ||
                    (buf[8] == 'm'.toByte() && buf[9] == 'p'.toByte() && buf[10] == '4'.toByte() && buf[11] == 'v'.toByte()) ||
                    (buf[8] == 'm'.toByte() && buf[9] == 'p'.toByte() && buf[10] == '7'.toByte() && buf[11] == '1'.toByte()) ||
                    (buf[8] == 'M'.toByte() && buf[9] == 'S'.toByte() && buf[10] == 'N'.toByte() && buf[11] == 'V'.toByte()) ||
                    (buf[8] == 'N'.toByte() && buf[9] == 'D'.toByte() && buf[10] == 'A'.toByte() && buf[11] == 'S'.toByte()) ||
                    (buf[8] == 'N'.toByte() && buf[9] == 'D'.toByte() && buf[10] == 'S'.toByte() && buf[11] == 'C'.toByte()) ||
                    (buf[8] == 'N'.toByte() && buf[9] == 'S'.toByte() && buf[10] == 'D'.toByte() && buf[11] == 'C'.toByte()) ||
                    (buf[8] == 'N'.toByte() && buf[9] == 'D'.toByte() && buf[10] == 'S'.toByte() && buf[11] == 'H'.toByte()) ||
                    (buf[8] == 'N'.toByte() && buf[9] == 'D'.toByte() && buf[10] == 'S'.toByte() && buf[11] == 'M'.toByte()) ||
                    (buf[8] == 'N'.toByte() && buf[9] == 'D'.toByte() && buf[10] == 'S'.toByte() && buf[11] == 'P'.toByte()) ||
                    (buf[8] == 'N'.toByte() && buf[9] == 'D'.toByte() && buf[10] == 'S'.toByte() && buf[11] == 'S'.toByte()) ||
                    (buf[8] == 'N'.toByte() && buf[9] == 'D'.toByte() && buf[10] == 'X'.toByte() && buf[11] == 'C'.toByte()) ||
                    (buf[8] == 'N'.toByte() && buf[9] == 'D'.toByte() && buf[10] == 'X'.toByte() && buf[11] == 'H'.toByte()) ||
                    (buf[8] == 'N'.toByte() && buf[9] == 'D'.toByte() && buf[10] == 'X'.toByte() && buf[11] == 'M'.toByte()) ||
                    (buf[8] == 'N'.toByte() && buf[9] == 'D'.toByte() && buf[10] == 'X'.toByte() && buf[11] == 'P'.toByte()) ||
                    (buf[8] == 'N'.toByte() && buf[9] == 'D'.toByte() && buf[10] == 'X'.toByte() && buf[11] == 'S'.toByte()) ||
                    (buf[8] == 'F'.toByte() && buf[9] == '4'.toByte() && buf[10] == 'V'.toByte() && buf[11] == ' '.toByte()) ||
                    (buf[8] == 'F'.toByte() && buf[9] == '4'.toByte() && buf[10] == 'P'.toByte() && buf[11] == ' '.toByte()))
}