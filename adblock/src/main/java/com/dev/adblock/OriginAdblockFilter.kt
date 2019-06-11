package com.dev.adblock

import android.os.Environment
import java.util.HashMap
import java.util.LinkedList
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

object OriginAdblockFilter {
    private var initialized = false //是否已经初始化完成
    private val m = 8  //ruleHashMap的key的默认长度为8，不足8的保留再特殊的rule List之中
    private val whiteListHashMap = HashMap<String, Rule>()
    private val ruleHashMap = HashMap<String, Rule>()
    private val jumpHashMap = HashMap<String, Int>()
    private val readWriteLock = ReentrantReadWriteLock()
    //初始化
    fun initialize(localPath: String, remotePath: String) {
        readWriteLock.writeLock().lock()

        //TODO:read file or read remote file
        //TODO:translate line to rule
        //TODO:extract rule to key
        //TODO:save them
        initialized = true
        readWriteLock.writeLock().unlock()
    }
    //获取文件
    private fun readFile(localPath:String,remotePath:String){

    }
    //是否再白名单中
    fun isInWhiteList(url:String):Boolean{

        return false
    }
    //获取匹配的过滤器
    //如果没有初始化完成，不需要屏蔽
    fun isInAdblockList(url: String): Boolean {
        readWriteLock.readLock().lock()
        if (!initialized)
            return false
        //TODO:get match filters
        readWriteLock.readLock().unlock()
        return false
    }


    //TODO:从规则中获取
    private fun extractKeyFromRule(rule: Rule): String {

        return ""
    }
}

