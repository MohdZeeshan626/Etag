package com.max360group.cammax360.repository.room

import android.content.Context
import androidx.room.*
import com.max360group.cammax360.repository.models.*
import com.max360group.cammax360.repository.room.daos.*

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [User::class, Job::class, AccountList::class, JobMediaList::class, RolesList::class, UserOwner::class,
        PropertyDetail::class,IntegrationData::class], version = 2, exportSchema = false
)

@TypeConverters(Converter::class)
abstract class CamMaxRoomDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun jobsDao(): JobsDao
    abstract fun membersDao(): MembersDao
    abstract fun media(): MediaDao
    abstract fun rolesDao(): RolesDao
    abstract fun ownersDao(): OwnersDao
    abstract fun propertyDoa(): PropertiesDao
    abstract fun integrationCommonsDoa(): IntegrationCommonsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: CamMaxRoomDatabase? = null
        fun getDatabase(context: Context): CamMaxRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CamMaxRoomDatabase::class.java,
                    "cam_max_room_database"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}