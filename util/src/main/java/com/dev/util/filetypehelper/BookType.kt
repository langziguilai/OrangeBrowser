package com.dev.util.filetypehelper
const val BOOK_MAX_BYTE=60
fun Epub(buf :ByteArray) :Boolean {
    return buf.size > 57 &&
            buf[0] == 0x50.toByte() && buf[1] == 0x4B.toByte() && buf[2] == 0x3.toByte() && buf[3] == 0x4.toByte() &&
            buf[30] == 0x6D.toByte() && buf[31] == 0x69.toByte() && buf[32] == 0x6D.toByte() && buf[33] == 0x65.toByte() &&
            buf[34] == 0x74.toByte() && buf[35] == 0x79.toByte() && buf[36] == 0x70.toByte() && buf[37] == 0x65.toByte() &&
            buf[38] == 0x61.toByte() && buf[39] == 0x70.toByte() && buf[40] == 0x70.toByte() && buf[41] == 0x6C.toByte() &&
            buf[42] == 0x69.toByte() && buf[43] == 0x63.toByte() && buf[44] == 0x61.toByte() && buf[45] == 0x74.toByte() &&
            buf[46] == 0x69.toByte() && buf[47] == 0x6F.toByte() && buf[48] == 0x6E.toByte() && buf[49] == 0x2F.toByte() &&
            buf[50] == 0x65.toByte() && buf[51] == 0x70.toByte() && buf[52] == 0x75.toByte() && buf[53] == 0x62.toByte() &&
            buf[54] == 0x2B.toByte() && buf[55] == 0x7A.toByte() && buf[56] == 0x69.toByte() && buf[57] == 0x70.toByte()
}

fun Pdf(buf :ByteArray) :Boolean {
    return buf.size > 3 &&
            buf[0] == 0x25.toByte() && buf[1] == 0x50.toByte() &&
            buf[2] == 0x44.toByte() && buf[3] == 0x46.toByte()
}