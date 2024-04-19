package com.kanzankazu.kanzandatabase.room

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
abstract class BaseRepository<T> {
    abstract val dao: BaseDao<T>
}
