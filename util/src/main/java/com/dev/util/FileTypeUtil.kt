package com.dev.util

import com.dev.util.filetypehelper.*
//借鉴的库：https://github.com/h2non/filetype/blob/master/matchers/document.go
object FileTypeUtil {
    //检查是否是视频文件
    fun isVideo(buf: ByteArray): Boolean {
        return Mp4(buf)
                || M4v(buf)
                || Mkv(buf)
                || Webm(buf)
                || Mov(buf)
                || Avi(buf)
                || Wmv(buf)
                || Mpeg(buf)
                || Flv(buf)
    }
    //检查图片
    fun isImage(buf:ByteArray):Boolean{
        return Jpeg(buf)
                || Jpeg2000(buf)
                ||Png(buf)
                ||Gif(buf)
                ||Webp(buf)
                ||CR2(buf)
                ||Tiff(buf)
                ||Bmp(buf)
                ||Jxr(buf)
                ||Psd(buf)
                ||Ico(buf)
                ||Dwg(buf)
    }
    //检查音频
    fun isAudio(buf:ByteArray):Boolean{
        return Midi(buf)
                ||Mp3(buf)
                ||M4a(buf)
                ||Ogg(buf)
                ||Flac(buf)
                ||Wav(buf)
                ||Amr(buf)
                ||Aac(buf)
    }
    //压缩文件
    fun isArchive(buf: ByteArray):Boolean{
        return Zip(buf)
                ||Tar(buf)
                ||Rar(buf)
                ||Gz(buf)
                ||Bz2(buf)
                ||SevenZ(buf)
                ||Exe(buf)
                ||Swf(buf)
                ||Rtf(buf)
                ||Nes(buf)
                ||Crx(buf)
                ||Cab(buf)
                ||Eot(buf)
                ||Ps(buf)
                ||Xz(buf)
                ||Sqlite(buf)
                ||Deb(buf)
                ||Ar(buf)
                ||Z(buf)
                ||Lz(buf)
                ||Rpm(buf)
                ||Elf(buf)
                ||Dcm(buf)
    }
}