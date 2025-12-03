package cn.wzpmc.utils.stream

import lombok.Getter
import java.io.IOException
import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest

@Getter
open class SizeStatisticsDigestInputStream(stream: InputStream, digest: MessageDigest) :
    DigestInputStream(stream, digest) {
    var size: Long = 0

    @Throws(IOException::class)
    override fun read(): Int {
        val read = super.read()
        if (read != -1) size++
        return read
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val read = super.read(b, off, len)
        if (read != -1) size += read.toLong()
        return read
    }
}
