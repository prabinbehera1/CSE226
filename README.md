# Travel Diary App
The Travel Diary App is a mobile application designed to be your digital companion on your journeys, allowing you to capture and cherish your travel experiences effortlessly. Built using Room Database, this app offers a seamless and intuitive interface for users to document their adventures, store memories, and revisit them whenever they desire.


## Screenshots

![App Screenshot](https://github.com/ZurichBlade/TraveDiaryApp/raw/master/Screenshot%20travel%20diary.png)


## Features

- Room Database: Facilitating efficient and secure data storage and management.
- Multiple Users: Empowering multiple individuals to manage their diaries and content separately.
- GPS Location Integration: Automatically fetching the current location when creating a diary entry.


## Query Examples

Defining a table wiht @Entity 

```bash
 @Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val username: String,
    val email: String,
    val password: String
)
```

Creating and Initializing a Database annotate with @Database 

```bash
@Database(entities = [User::class, DiaryEntries::class, Photos::class], version = 1, exportSchema = false)
```
```bash
  fun getDatabase(context: Context): MyDatabase {

            INSTANCE?.let {
                return it
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "my_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
```

Query to check if user already exist under a interface using @Dao annotation

```bash
   @Query("SELECT EXISTS(SELECT * FROM user_table WHERE userName = :userName and password = :password)")
    suspend fun isUserExists(userName: String?, password: String): Boolean
```


## Note
- This project demonstrates the basic integration of Room database in Android using Kotlin language and XML-based layouts.
