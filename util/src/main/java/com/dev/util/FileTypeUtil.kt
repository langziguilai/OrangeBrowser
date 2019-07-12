package com.dev.util

import com.dev.util.filetypehelper.*
import java.io.File
import java.io.FileInputStream

//借鉴的库：https://github.com/h2non/filetype/blob/master/matchers/document.go
class FileTypeUtil {
    companion object FileTypeUtil {
        //检查是否是视频文件
        fun isVideo(path: String): Boolean {
            val buf = ByteArray(VIDEO_MAX_BYTE)
            var fis:FileInputStream?=null
            try {
                fis = FileInputStream(File(path))
                fis.read(buf, 0, VIDEO_MAX_BYTE)
            } catch (e: Exception) {
                e.printStackTrace()
            }finally {
                fis?.close()
            }
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
        fun isImage(path: String): Boolean {
            val buf = ByteArray(IMAGE_MAX_BYTE)
            try {
                val fis = FileInputStream(File(path))
                fis.read(buf, 0, IMAGE_MAX_BYTE)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return Jpeg(buf)
                    || Jpeg2000(buf)
                    || Png(buf)
                    || Gif(buf)
                    || Webp(buf)
                    || CR2(buf)
                    || Tiff(buf)
                    || Bmp(buf)
                    || Jxr(buf)
                    || Psd(buf)
                    || Ico(buf)
                    || Dwg(buf)
        }

        //检查音频
        fun isAudio(path: String): Boolean {
            val buf = ByteArray(AUDIO_MAX_BYTE)
            try {
                val fis = FileInputStream(File(path))
                fis.read(buf, 0, AUDIO_MAX_BYTE)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return Midi(buf)
                    || Mp3(buf)
                    || M4a(buf)
                    || Ogg(buf)
                    || Flac(buf)
                    || Wav(buf)
                    || Amr(buf)
                    || Aac(buf)
        }

        //压缩文件
        fun isArchive(path: String): Boolean {
            val buf = ByteArray(ARCHIVE_MAX_BYTE)
            try {
                val fis = FileInputStream(File(path))
                fis.read(buf, 0, ARCHIVE_MAX_BYTE)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return Zip(buf)
                    || Tar(buf)
                    || Rar(buf)
                    || Gz(buf)
                    || Bz2(buf)
                    || SevenZ(buf)
                    || Exe(buf)
                    || Swf(buf)
                    || Rtf(buf)
                    || Nes(buf)
                    || Crx(buf)
                    || Cab(buf)
                    || Eot(buf)
                    || Ps(buf)
                    || Xz(buf)
                    || Sqlite(buf)
                    || Deb(buf)
                    || Ar(buf)
                    || Z(buf)
                    || Lz(buf)
                    || Rpm(buf)
                    || Elf(buf)
                    || Dcm(buf)
        }
    }
}